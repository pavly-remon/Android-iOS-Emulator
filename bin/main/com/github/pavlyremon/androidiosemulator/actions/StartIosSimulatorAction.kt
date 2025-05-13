package com.PiCode.androidiosemulator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.JOptionPane

class StartIosSimulatorAction : AnAction("Start iOS Simulator") {
    override fun actionPerformed(e: AnActionEvent) {
        val osName = System.getProperty("os.name").lowercase()
        if (osName.contains("mac")) {
            try {
                ProcessBuilder("open", "-a", "Simulator.app")
                    .redirectErrorStream(true)
                    .start()
                JOptionPane.showMessageDialog(null, "iOS Simulator starting...")
            } catch (ex: Exception) {
                JOptionPane.showMessageDialog(null, "Failed to start iOS Simulator: ${ex.message}")
            }
        } else {
            JOptionPane.showMessageDialog(null, "iOS Simulator is only available on macOS.")
        }
    }
}
