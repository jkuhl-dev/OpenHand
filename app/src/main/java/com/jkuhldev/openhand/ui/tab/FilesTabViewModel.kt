package com.jkuhldev.openhand.ui.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
 * @property printer Printer associated with this ViewModel
 * @property scope Scope used for launching background operations
 */
class FilesTabViewModel(
    private val printer: Printer,
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
     * Preview constructor
     * @param printer Printer to be used in the preview ViewModel
     * @param currentPath Current path to be used in the preview ViewModel
     * @param files List of files to be used in the preview ViewModel
     * @param isLoading Loading state to be used in the preview ViewModel
     */
    constructor(
        printer: Printer,
        currentPath: String,
        files: List<FTPFile>,
        isLoading: Boolean
    ) : this(printer) {
        _currentPath.value = currentPath
        _files.value = files
        _isLoading.value = isLoading
    }

    /**
     * Lifecycle method called when the ViewModel's host closes
     */
    override fun onCleared() {
        scope.launch { printerFilesClient.stop() }
        scope.cancel()
    }

    /**
     * Changes the current directory
     * @param targetDirectory Path to the directory we want to change to
     */
    fun changeDirectory(targetDirectory: String) {
        scope.launch {
            _isLoading.value = true
            runCatching { printerFilesClient.changeDirectory(targetDirectory) }
                .onFailure { e ->
                    _exception.value = e
                    _isLoading.value = false
                }
                .onSuccess { refresh() }
        }
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
     */
    fun startClient() {
        scope.launch {
            runCatching { printerFilesClient.start(printer) }
                .onFailure { e ->
                    _exception.value = e
                    _isLoading.value = false
                }
                .onSuccess { refresh() }
        }
    }

    /**
     * Factory for creating FilesTabViewModel
     * @param printer Printer associated with this ViewModel
     */
    class Factory(private val printer: Printer) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FilesTabViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FilesTabViewModel(printer) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}