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

@PageTitle("AI Image Generator")
@Route("")
public class ImageGenerationView extends VerticalLayout {

    private final Image image;
    private final Image imagealt;
    private byte[] bytes;
    private StreamResource streamResource;

    public ImageGenerationView(ImageGenerationService imageGenerationService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("AI Image Generator");

        TextField promptTextField = new TextField("Image Description: ");

        ComboBox<Object> styleBox = new ComboBox<>("Style");
        styleBox.setItems("realistic","anime","flux-dev");
        styleBox.setValue("realistic");
        styleBox.setMinWidth(20, Unit.PIXELS);

        ComboBox<Object> sizeBox = new ComboBox<>("Size");
        sizeBox.setItems("1:1", "3:2", "4:3", "3:4", "16:9", "9:16");
        sizeBox.setValue("4:3");
        sizeBox.setMinWidth(20, Unit.PIXELS);

        image = new Image();
        image.setMaxHeight("500px");
        image.setMaxWidth("500px");

        imagealt = new Image();
        imagealt.setMaxHeight("500px");
        imagealt.setMaxWidth("500px");

        Button saveButton = new Button("Save Image");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        Button generateButton = new Button("Generate");
        promptTextField.addKeyPressListener(Key.ENTER, event -> {
            if (streamResource != null) {
                imagealt.setSrc(streamResource);
                bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
                streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
                image.setSrc(streamResource);
            }else {
                bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
                streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
                image.setSrc(streamResource);
            }

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
                imagealt.setSrc(streamResource);
                bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
                streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
                image.setSrc(streamResource);
            }else {
                bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue().toString(), sizeBox.getValue().toString());
                streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(bytes));
                image.setSrc(streamResource);
            }

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

        add(title, promptLayout, buttonLayout, image, imagealt);
    }

}