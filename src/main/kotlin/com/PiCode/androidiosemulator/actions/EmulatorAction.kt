package com.PiCode.androidiosemulator.actions

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.ui.Messages
import com.PiCode.androidiosemulator.settings.EmulatorSettingsConfigurable

class EmulatorAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val options = arrayOf("Android Emulator", "iOS Simulator")
        val choice = Messages.showDialog(
            "Select Emulator Type",
            "Emulator Selection",
            options,
            0, // Default option index
            null
        )

        when (choice) {
            0 -> startAndroidEmulator()
            1 -> startIosSimulator()
        }
    }

    private fun startAndroidEmulator() {
        val propertiesComponent = PropertiesComponent.getInstance()
        val sdkKey = "android.sdk.path"
        val sdkPath = propertiesComponent.getValue(sdkKey)
        
        if (sdkPath.isNullOrEmpty()) {
            val openSettings = Messages.showYesNoDialog(
                "Android SDK path is not set. Would you like to configure it in settings?",
                "SDK Path Not Found",
                "Open Settings",
                "Cancel",
                null
            )
            
            if (openSettings == Messages.YES) {
                ApplicationManager.getApplication().invokeLater {
                    ShowSettingsUtil.getInstance().showSettingsDialog(null, EmulatorSettingsConfigurable::class.java)
                }
            }
            return
        }

        val emulatorPath = "$sdkPath/emulator/emulator"
        val avdList = getAvailableAvds(emulatorPath)
        
        if (avdList.isEmpty()) {
            Messages.showErrorDialog("No AVDs found in the specified SDK path.", "Error")
            return
        }

        val avdChoice = Messages.showEditableChooseDialog(
            "Select an AVD",
            "Available AVDs",
            null,
            avdList.toTypedArray(),
            avdList.firstOrNull() ?: "",
            null
        )

        if (avdChoice != null) {
            try {
                ProcessBuilder(emulatorPath, "-avd", avdChoice)
                    .redirectErrorStream(true)
                    .start()
                Messages.showInfoMessage("Android Emulator starting... ($avdChoice)", "Success")
            } catch (e: Exception) {
                Messages.showErrorDialog("Failed to start Android Emulator: ${e.message}", "Error")
            }
        }
    }

    private fun getAvailableAvds(emulatorPath: String): List<String> {
        return try {
            val process = ProcessBuilder(emulatorPath, "-list-avds")
                .redirectErrorStream(true)
                .start()
            process.inputStream.bufferedReader().readLines()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun startIosSimulator() {
        val osName = System.getProperty("os.name").lowercase()
        if (!osName.contains("mac")) {
            Messages.showErrorDialog("iOS Simulator is only available on macOS.", "Error")
            return
        }

        val simulatorList = getAvailableIosSimulators()
        if (simulatorList.isEmpty()) {
            Messages.showErrorDialog("No iOS Simulators found.", "Error")
            return
        }

        val simulatorChoice = Messages.showEditableChooseDialog(
            "Select an iOS Simulator",
            "Available Simulators",
            null,
            simulatorList.toTypedArray(),
            simulatorList.firstOrNull() ?: "",
            null
        )

        if (simulatorChoice != null) {
            try {
                ProcessBuilder("xcrun", "simctl", "boot", simulatorChoice)
                    .redirectErrorStream(true)
                    .start()
                Messages.showInfoMessage("iOS Simulator starting... ($simulatorChoice)", "Success")
            } catch (e: Exception) {
                Messages.showErrorDialog("Failed to start iOS Simulator: ${e.message}", "Error")
            }
        }
    }

    private fun getAvailableIosSimulators(): List<String> {
        return try {
            val process = ProcessBuilder("xcrun", "simctl", "list", "devices", "--json")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText()
            // Parse JSON to extract simulator names (simplified for brevity)
            Regex("\"name\"\\s*:\\s*\"([^\"]+)\"").findAll(output).map { it.groupValues[1] }.toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
