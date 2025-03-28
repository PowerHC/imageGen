package com.example.application;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.xml.bind.DatatypeConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

@PageTitle("AI Image Generator")
@Route("")
public class ImageGenerationView extends VerticalLayout {

    private byte[] bytes;
    private final Image currentImage;
    private final Select<String> sizeBox;
    private final Select<String> styleBox;
    private StreamResource streamResource;
    private final TextField promptTextField;
    private final VerticalLayout historyLayout;

    public ImageGenerationView(ImageGenerationService imageGenerationService) {
        historyLayout = new VerticalLayout();
        historyLayout.setSizeFull();
        historyLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setAlignItems(Alignment.CENTER);

        H1 title = new H1("AI Image Generator");

        promptTextField = new TextField("Image Description");
        promptTextField.setMinWidth(450, Unit.PIXELS);

        styleBox = new Select<>();
        styleBox.setLabel("Style");
        styleBox.setItems("realistic","anime","imagine-turbo");
        styleBox.setValue("realistic");
        styleBox.setMaxWidth(160, Unit.PIXELS);

        sizeBox = new Select<>();
        sizeBox.setLabel("Size");
        sizeBox.setItems("1:1", "3:2", "4:3", "3:4", "16:9", "9:16");
        sizeBox.setValue("4:3");
        sizeBox.setMaxWidth(85, Unit.PIXELS);

        currentImage = new Image();
        currentImage.setMaxHeight("500px");
        currentImage.setMaxWidth("500px");

        Button saveButton = new Button("Save Image");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);

        Button generateButton = new Button("Generate");
        generateButton.addClickListener(event -> {
            generateImage(imageGenerationService);

            saveButton.setEnabled(true);
            saveButton.addClickListener(e -> {
                try {
                    imageGenerationService.saveImage(bytes, promptTextField.getValue());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
        promptTextField.addKeyPressListener(Key.ENTER, event -> {
            generateImage(imageGenerationService);

            saveButton.setEnabled(true);
            saveButton.addClickListener(e -> {
                try {
                    imageGenerationService.saveImage(bytes, promptTextField.getValue());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        });

        Button getHistory = new Button("Get History");
        getHistory.addClickListener(event -> {
           currentImage.setSrc("");
           historyLayout.removeAll();
           getImages(imageGenerationService);
        });

        HorizontalLayout promptLayout = new HorizontalLayout();
        promptLayout.add(promptTextField, styleBox, sizeBox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.add(generateButton, saveButton, getHistory);

        verticalLayout.add(title, promptLayout, buttonLayout, currentImage, historyLayout);

        add(verticalLayout);
    }

    private void generateImage(ImageGenerationService imageGenerationService) {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        if (streamResource != null) {
            Image historyImage = new Image(currentImage.getSrc(), formattedTime);
            historyImage.setMaxHeight("500px");
            historyImage.setMaxWidth("500px");
            historyLayout.addComponentAtIndex(0, historyImage);
        }
        bytes = imageGenerationService.generateImageResource(promptTextField.getValue(), styleBox.getValue(), sizeBox.getValue());
        streamResource = new StreamResource(formattedTime + ".png", () -> new ByteArrayInputStream(bytes));
        currentImage.setSrc(streamResource);

        String str = Base64.getEncoder().encodeToString(bytes);
        String response = ImageGenerationService.postToRest(str, formattedTime);
    }

    private void getImages(ImageGenerationService imageHistoryService) {
        String[] imageSrcs = imageHistoryService.getAllImages();

        if (Arrays.equals(imageSrcs, new String[]{"Something went wrong!"})) {
            H1 err = new H1("Something went wrong!");
            historyLayout.addComponentAtIndex(0, err);

            return;
        }

        for (String imageSrc : imageSrcs) {
            byte[] image_bytes = DatatypeConverter.parseBase64Binary(imageSrc);
            StreamResource streamResource = new StreamResource("image.png", () -> new ByteArrayInputStream(image_bytes));

            Image historyImage = new Image(streamResource, "");
            historyImage.setMaxHeight("500px");
            historyImage.setMaxWidth("500px");
            historyLayout.addComponentAtIndex(0, historyImage);
        }
    }

}