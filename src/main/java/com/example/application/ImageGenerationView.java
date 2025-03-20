package com.example.application;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@PageTitle("AI Image Generator")
@Route("")
public class ImageGenerationView extends VerticalLayout {

    private byte[] bytes;
    private StreamResource streamResource;

    public ImageGenerationView(ImageGenerationService imageGenerationService) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(Alignment.CENTER);

        H1 title = new H1("AI Image Generator");

        TextField promptTextField = new TextField("Image Description");
        promptTextField.setMinWidth(450, Unit.PIXELS);

        ComboBox<Object> styleBox = new ComboBox<>("Style");
        styleBox.setItems("realistic","anime","flux-dev");
        styleBox.setValue("realistic");
        styleBox.setMaxWidth(135, Unit.PIXELS);

        ComboBox<Object> sizeBox = new ComboBox<>("Size");
        sizeBox.setItems("1:1", "3:2", "4:3", "3:4", "16:9", "9:16");
        sizeBox.setValue("4:3");
        sizeBox.setMaxWidth(105, Unit.PIXELS);

        Image currentImage = new Image();
        currentImage.setMaxHeight("500px");
        currentImage.setMaxWidth("500px");

        Button saveButton = new Button("Save Image");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        Button generateButton = new Button("Generate");
        promptTextField.addKeyPressListener(Key.ENTER, event -> {
            if (streamResource != null) {
                Image historyImage = new Image(currentImage.getSrc(), "Generated image history");
                historyImage.setMaxHeight("500px");
                historyImage.setMaxWidth("500px");
                verticalLayout.add(historyImage);
            }
            bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
            streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
            currentImage.setSrc(streamResource);

            saveButton.setEnabled(true);
            saveButton.addClickListener(e -> {
                try {
                    imageGenerationService.saveImage(bytes, promptTextField.getValue());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
        generateButton.addClickListener(event -> {
            if (streamResource != null) {
                Image historyImage = new Image(currentImage.getSrc(), "Generated image history");
                historyImage.setMaxHeight("500px");
                historyImage.setMaxWidth("500px");
                verticalLayout.add(historyImage);
            }
            bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
            streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
            currentImage.setSrc(streamResource);

            saveButton.setEnabled(true);
            saveButton.addClickListener(e -> {
                try {
                    imageGenerationService.saveImage(bytes, promptTextField.getValue());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });

        HorizontalLayout promptLayout = new HorizontalLayout();
        promptLayout.add(promptTextField, styleBox, sizeBox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(generateButton, saveButton);

        verticalLayout.add(title, promptLayout, buttonLayout, currentImage);

        add(verticalLayout);
    }

}