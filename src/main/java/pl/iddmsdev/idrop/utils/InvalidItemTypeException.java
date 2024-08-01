package pl.iddmsdev.idrop.utils;

public class InvalidItemTypeException extends Exception {
    public InvalidItemTypeException(String path, String name, String currentType) {
        super(
                "[iDrop] It's impossible to create one item. Here's more info:\n" +
                "Current type: " + currentType + "\n" +
                "Path: " + path + "\n" +
                "File: " + name + "\n"
        );
    }
}
