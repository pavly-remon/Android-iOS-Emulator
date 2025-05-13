package com.PiCode.androidiosemulator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.JOptionPane

class StartAndroidEmulatorAction : AnAction("Start Android Emulator") {
    override fun actionPerformed(e: AnActionEvent) {
        val avdName = "Pixel_4_API_30" // Change this to your AVD name
        try {
            ProcessBuilder("emulator", "-avd", avdName)
                .redirectErrorStream(true)
                .start()
            JOptionPane.showMessageDialog(null, "Android Emulator starting... ($avdName)")
        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(null, "Failed to start Android Emulator: ${ex.message}")
        }
    }
}
