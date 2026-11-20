package me.suprun.gui;

import me.suprun.core.tasks.Task;
import me.suprun.core.tasks.TaskService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public final class TaskWindow {
    private final TaskService tasks;
    private final DefaultListModel<Task> listModel = new DefaultListModel<>();
    private final JList<Task> taskList = new JList<>(listModel);
    private final JLabel titleLabel = new JLabel("Оберіть завдання зі списку");
    private final JLabel statusLabel = new JLabel(" ");
    private final JTextArea detailsArea = new JTextArea(5, 20);
    private final JTextField titleField = new JTextField();
    private final JTextArea descriptionField = new JTextArea(3, 20);
    private final JButton completeButton = new JButton("Позначити виконаним");
    private final JButton deleteButton = new JButton("Видалити");
    private JFrame frame;

    public TaskWindow(TaskService tasks) {
        this.tasks = tasks;
    }

    public void show() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Графічний інтерфейс потрібно запускати в потоці Swing.");
        }

        frame = new JFrame("Task Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(720, 520));
        frame.setSize(800, 580);
        frame.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createTaskArea(), BorderLayout.CENTER);
        content.add(createForm(), BorderLayout.SOUTH);
        frame.setContentPane(content);

        refreshTasks(null);
        frame.setVisible(true);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JLabel heading = new JLabel("Мої завдання");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JLabel("Переглядайте, створюйте та відмічайте виконані завдання."), BorderLayout.SOUTH);
        return panel;
    }

    private JSplitPane createTaskArea() {
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        taskList.setCellRenderer((list, task, index, selected, focus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, task, index, selected, focus);
            String status = task.completed() ? " [виконано]" : " [не виконано]";
            label.setText("#%d  %s%s".formatted(task.id(), task.title(), status));
            label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            return label;
        });
        taskList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                showSelectedTask();
            }
        });

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Список завдань"));
        listPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel detailsPanel = createDetailsPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, detailsPanel);
        splitPane.setResizeWeight(0.45);
        return splitPane;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Інформація про завдання"));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);

        JPanel information = new JPanel(new BorderLayout(4, 8));
        information.add(titleLabel, BorderLayout.NORTH);
        information.add(statusLabel, BorderLayout.CENTER);
        information.add(new JScrollPane(detailsArea), BorderLayout.SOUTH);
        panel.add(information, BorderLayout.CENTER);

        completeButton.addActionListener(event -> completeSelectedTask());
        deleteButton.addActionListener(event -> deleteSelectedTask());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.add(completeButton);
        actions.add(deleteButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createForm() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Нове завдання"));

        JPanel fields = new JPanel(new GridLayout(2, 1, 4, 4));
        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.add(new JLabel("Назва:"), BorderLayout.WEST);
        titleRow.add(titleField, BorderLayout.CENTER);
        JPanel descriptionRow = new JPanel(new BorderLayout(8, 0));
        descriptionRow.add(new JLabel("Опис:"), BorderLayout.WEST);
        descriptionRow.add(new JScrollPane(descriptionField), BorderLayout.CENTER);
        fields.add(titleRow);
        fields.add(descriptionRow);
        panel.add(fields, BorderLayout.CENTER);

        JButton createButton = new JButton("Створити завдання");
        createButton.addActionListener(event -> createTask());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(createButton);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void createTask() {
        try {
            Task task = tasks.create(titleField.getText(), descriptionField.getText());
            titleField.setText("");
            descriptionField.setText("");
            refreshTasks(task.id());
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void completeSelectedTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            return;
        }

        try {
            tasks.complete(selected.id());
            refreshTasks(selected.id());
        } catch (RuntimeException exception) {
            showError(exception);
            refreshTasks(selected.id());
        }
    }

    private void deleteSelectedTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Видалити завдання «%s»?".formatted(selected.title()),
                "Підтвердження видалення",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            tasks.delete(selected.id());
            refreshTasks(null);
        } catch (RuntimeException exception) {
            showError(exception);
            refreshTasks(selected.id());
        }
    }

    private void refreshTasks(Long selectedId) {
        listModel.clear();
        int selectedIndex = -1;
        for (Task task : tasks.findAll()) {
            listModel.addElement(task);
            if (selectedId != null && task.id() == selectedId) {
                selectedIndex = listModel.size() - 1;
            }
        }

        if (selectedIndex >= 0) {
            taskList.setSelectedIndex(selectedIndex);
        } else if (!listModel.isEmpty()) {
            taskList.setSelectedIndex(0);
        } else {
            taskList.clearSelection();
            showSelectedTask();
        }
    }

    private void showSelectedTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            titleLabel.setText("Оберіть завдання зі списку");
            statusLabel.setText(" ");
            detailsArea.setText("");
            completeButton.setEnabled(false);
            deleteButton.setEnabled(false);
            return;
        }

        titleLabel.setText("#%d  %s".formatted(selected.id(), selected.title()));
        statusLabel.setText(selected.completed() ? "Статус: виконано" : "Статус: не виконано");
        detailsArea.setText(selected.description().isBlank() ? "Опис не вказано." : selected.description());
        completeButton.setEnabled(!selected.completed());
        deleteButton.setEnabled(true);
    }

    private void showError(RuntimeException exception) {
        String message = exception.getMessage();
        JOptionPane.showMessageDialog(
                frame,
                message == null || message.isBlank() ? "Не вдалося виконати операцію." : message,
                "Помилка",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
