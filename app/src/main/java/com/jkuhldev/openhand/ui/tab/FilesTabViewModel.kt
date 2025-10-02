package com.jkuhldev.openhand.ui.tab

import androidx.lifecycle.ViewModel
import com.jkuhldev.openhand.data.Printer
import com.jkuhldev.openhand.network.PrinterFilesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.net.ftp.FTPFile

/**
 * ViewModel for FilesTab
 * @property scope Scope used for launching background operations
 */
class FilesTabViewModel(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : ViewModel() {
    private val _currentPath: MutableStateFlow<String> = MutableStateFlow("")
    private val _exception: MutableStateFlow<Throwable?> = MutableStateFlow(null)
    private val _files: MutableStateFlow<List<FTPFile>> = MutableStateFlow(listOf())
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val printerFilesClient = PrinterFilesClient()

    val currentPath: StateFlow<String> = _currentPath.asStateFlow()
    val exception: StateFlow<Throwable?> = _exception.asStateFlow()
    val files: StateFlow<List<FTPFile>> = _files.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Lifecycle method called when the ViewModel's host closes
     */
    override fun onCleared() {
        scope.launch { printerFilesClient.stop() }
        scope.cancel()
    }

    /**
     * Refreshes the current path and file list states using data from the PrinterFilesClient
     */
    fun refresh() {
        scope.launch {
            _isLoading.value = true
            runCatching {
                _currentPath.value = printerFilesClient.getCurrentDirectory()
                _files.value = printerFilesClient.listCurrentDirectory()
            }.onFailure { e -> _exception.value = e }
            _isLoading.value = false
        }
    }

    /**
     * Starts the PrinterFilesClient managed by the ViewModel
     * @param printer Printer that is being interacted with
     * @param isPreview Boolean that denotes if the ViewModel should use preview data or not
     */
    fun startClient(printer: Printer, isPreview: Boolean = false) {
        if (isPreview) {
            _currentPath.value = "/preview/directory"
            _files.value = listOf(
                FTPFile().apply { name = "Preview_File1.3mf" },
                FTPFile().apply { name = "Preview_File2.zip" },
                FTPFile().apply { name = "Preview_File3.mp4" }
            )
            _isLoading.value = false
            return
        }

        scope.launch {
            runCatching { printerFilesClient.start(printer) }
                .onFailure {
                    e -> _exception.value = e
                    _isLoading.value = false
                }
                .onSuccess { refresh() }
        }
    }
}