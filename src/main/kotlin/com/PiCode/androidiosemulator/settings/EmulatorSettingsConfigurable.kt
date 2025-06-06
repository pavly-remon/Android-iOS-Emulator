package com.PiCode.androidiosemulator.settings

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel

class EmulatorSettingsConfigurable : Configurable {

    private val propertiesComponent = PropertiesComponent.getInstance()
    private val sdkKey = "android.sdk.path"
    private val panel = JPanel()
    private val sdkPathField = TextFieldWithBrowseButton()

    init {
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val label = JLabel("Set Android SDK Path:")
        label.alignmentX = JLabel.LEFT_ALIGNMENT

        val browseButton = JButton("Browse")
        browseButton.addActionListener {
            val fileChooser = JFileChooser()
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val result = fileChooser.showOpenDialog(panel)
            if (result == JFileChooser.APPROVE_OPTION) {
                sdkPathField.text = fileChooser.selectedFile.absolutePath
            }
        }
        sdkPathField.alignmentX = JLabel.LEFT_ALIGNMENT
        sdkPathField.maximumSize = Dimension(Int.MAX_VALUE, sdkPathField.preferredSize.height)

        panel.add(label)
        panel.add(Box.createRigidArea(Dimension(0, 5))) // Add spacing between label and input
        panel.add(sdkPathField)
        panel.add(Box.createRigidArea(Dimension(0, 5))) // Add spacing between input and button
        panel.add(browseButton)
    }

    override fun createComponent(): JComponent {
        sdkPathField.text = propertiesComponent.getValue(sdkKey, "")
        return panel
    }

    override fun isModified(): Boolean {
        return sdkPathField.text != propertiesComponent.getValue(sdkKey, "")
    }

    override fun apply() {
        propertiesComponent.setValue(sdkKey, sdkPathField.text)
    }

    override fun getDisplayName(): String {
        return "Android-iOS Emulator"
    }
}
