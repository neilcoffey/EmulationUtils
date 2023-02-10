/*
BSD 2-Clause License

Copyright (c) 2023, Neil Coffey

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.javamex.emutil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EMUtil extends JFrame {

    private final JComboBox<UtilFunctionType> functionChooser = new JComboBox<>(UtilFunctionType.values());
    private final Box paramsBox = Box.createVerticalBox();
    private final JTextArea outputField = new JTextArea(20, 80);
    private final Map<FileSpec, FileSpecifier> fileSpecifiers = new HashMap<>();
    private final Map<FileSpec, Path> selectedPaths = new HashMap<>();
    private UtilFunctionType selectedFunctionType;
    private Path previousLocation;

    private final Action axRunOp = createAction("Run", this::runSelectedOp);
    private final Action axQuit = createAction("Quit", this::quit);

    public static void main(String[] args) {
        EMUtil mainFrame = new EMUtil("Emulation ROM Patch/Checker Utility");

        SwingUtilities.invokeLater(() -> {
            mainFrame.setLocationByPlatform(true);
            mainFrame.setVisible(true);
        });
    }

    private EMUtil(String title) {
        super(title);
        installUI();
        selectedFunctionType = UtilFunctionType.values()[0];
        populateMainPane(selectedFunctionType);
        pack();
    }

    private void installUI() {
        add(createUtilChooserPane(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createButtonBar(), BorderLayout.SOUTH);
    }

    private JComponent createUtilChooserPane() {
        Box b = Box.createVerticalBox();
        addLabelled(b, functionChooser, "Operation");
        functionChooser.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                UtilFunctionType fnType = (UtilFunctionType) e.getItem();
                if (fnType != selectedFunctionType) {
                    selectedFunctionType = fnType;
                    populateMainPane(fnType);
                    pack();
                }
            }
        });
        return b;
    }

    private JComponent createMainPanel() {
        Box b = Box.createVerticalBox();
        b.add(paramsBox);
        JScrollPane sp = new JScrollPane(outputField);
        sp.setPreferredSize(new Dimension(820, 320));
        b.add(sp);
        return b;
    }

    private Map<UtilFunctionType, UtilFunctionParams> paramsByFunction = new HashMap<>();

    private void populateMainPane(UtilFunctionType functionType) {
        paramsBox.removeAll();
        fileSpecifiers.clear();

        UtilFunctionParams params;
        try {
            params = UtilFunctionParams.constructParams(functionType);
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }

        List<FileSpec> inputs = params.getInputSpecs();
        for (FileSpec input : inputs) {
            addFileSelector(paramsBox, input);
        }
        List<FileSpec> outputs = params.getOutputSpecs();
        for (FileSpec output : outputs) {
            addFileSelector(paramsBox, output);
        }
    }

    private void addFileSelector(Box b, FileSpec spec) {
        FileSpecifier fs = new FileSpecifier(spec);
        Path p = selectedPaths.get(spec);
        if (p != null) {
            fs.setPath(p);
        }
        fs.addTo(b);
    }

    private JComponent createButtonBar() {
        Box b = Box.createHorizontalBox();
        b.add(Box.createHorizontalGlue());
        b.add(new JButton(axQuit));
        b.add(Box.createVerticalStrut(2));
        b.add(new JButton(axRunOp));
        b.add(Box.createHorizontalGlue());
        return b;
    }

    private static Component addLabelled(Container cont, JComponent component, String labelText, Component... additional) {
        Box b = Box.createHorizontalBox();
        JLabel label = new JLabel(labelText, SwingConstants.TRAILING);
        Dimension labelDim = label.getPreferredSize();
        label.setPreferredSize(new Dimension(256 * ((labelDim.width + 255) / 256), labelDim.height));
        b.add(label);
        b.add(component);
        label.setLabelFor(component);
        for (Component c : additional) {
            b.add(c);
        }
        b.add(Box.createHorizontalGlue());
        cont.add(b);
        return b;
    }

    private void openFileBrowserFor(FileSpecifier fs) {
        JFileChooser jfc = new JFileChooser(new File("."));
        FileSpec fileSpec = fs.spec;
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (!f.isFile())
                    return false;
                Pattern patt = fileSpec.getFileNamePattern();
                if (patt != null) {
                    Matcher m = patt.matcher(f.getName());
                    return m.matches();
                }
                return true;
            }

            @Override
            public String getDescription() {
                return fs.spec.getFilePatternDesc();
            }
        });
        Path prevFile = fs.selectedPath;
        Path rootDir = null;
        if (prevFile != null) {
            rootDir = prevFile.getParent();
        } else {
            rootDir = previousLocation;
        }

        if (rootDir != null && Files.isDirectory(rootDir)) {
            try {
                jfc.setCurrentDirectory(rootDir.toFile());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int res = (fileSpec.isOutput()) ?
                jfc.showSaveDialog(this) :
                jfc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = jfc.getSelectedFile();
            if (f != null) {
                Path p = f.toPath();
                fs.setPath(p);
                selectedPaths.put(fileSpec, p);
                previousLocation = p.getParent();
            }
        }
    }

    private void runSelectedOp() {
        if (selectedFunctionType == null)
            return;
        outputField.setText("");

        try {
            UtilFunctionParams params = UtilFunctionParams.constructParams(selectedFunctionType);
            for (FileSpec input : params.getInputSpecs()) {
                Path p = selectedPaths.get(input);
                if (p != null) {
                    params.setInput(input, p);
                }
            }
            for (FileSpec output : params.getOutputSpecs()) {
                Path p = selectedPaths.get(output);
                if (p != null) {
                    if (Files.exists(p)) {
                        if (JOptionPane.showConfirmDialog(this,
                                "The file '" + p + "' exists. Overwrite?", "Confirm overwrite",
                                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    params.setOutput(output, p);
                }
            }
            params.validate();

            // At present, the operations in question will run very quickly, so just run in the EDT.
            // For longer-running operations, we would need to spawn a new thread.
            StringBuilder sb = new StringBuilder(1024);
            UtilFunction<?> fn = UtilFunction.getFunction(selectedFunctionType, params);
            String newline = String.format("%n");
            fn.runFunction(new ProgressConsumer() {
                @Override
                public void onDebugMessage(String msg) {
                    sb.append(msg).append(newline);
                }

                @Override
                public void onSourceError(FileSpec file, long fileOffset, String message) {
                    String formattedStr = String.format("Error in %s at 0x%08x : %s%n",
                            file.getDisplayName(), fileOffset, message);
                    sb.append(formattedStr);
                }

                @Override
                public void onFatalError(Throwable t) {
                    t.printStackTrace();
                    // Do in invokeLater so that other messages appear behind the dialog
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(EMUtil.this, "An error occurred running this operation: " + t.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            });
            outputField.setText(sb.toString());
        } catch (Throwable t) {
            String msg;
            if (t instanceof ParameterValidationException) {
                msg = t.getMessage();
            } else {
                msg = "Error running selected operation: " + t.getMessage();
            }
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quit() {
        System.exit(0);
    }

    private class FileSpecifier {
        private final FileSpec spec;
        private final JTextField pathField;
        private Path selectedPath;

        FileSpecifier(FileSpec spec) {
            this.spec = spec;
            this.pathField = new JTextField(24);
        }

        void addTo(Box b) {
            Action ax = createAction("Browse...", () -> openFileBrowserFor(this));
            addLabelled(b, pathField, spec.getDisplayName(), new JButton(ax));
        }

        void setPath(Path p) {
            pathField.setText(p.toString());
            selectedPath = p;
        }

    }

    private static Action createAction(String caption, Runnable r) {
        return new ActionImpl<>(caption, null, (x) -> r.run(), null);
    }

    private static class ActionImpl<T> extends AbstractAction {
        private final Consumer<T> actionRunnable;
        private final T val;
        ActionImpl(String name, String iconName, Consumer<T> actionRunnable, T val) {
            super(name);
            this.actionRunnable = actionRunnable;
            this.val = val;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            actionRunnable.accept(val);
        }
    }

}
