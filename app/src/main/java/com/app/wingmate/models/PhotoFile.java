package com.app.wingmate.models;

import com.parse.ParseFile;

import java.io.File;

public class PhotoFile {

    private File file;
    private ParseFile parseFile;
    private boolean isDummyFile;

    public PhotoFile() {

    }

    public ParseFile getParseFile() {
        return parseFile;
    }

    public void setParseFile(ParseFile parseFile) {
        this.parseFile = parseFile;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isDummyFile() {
        return isDummyFile;
    }

    public void setDummyFile(boolean dummyFile) {
        isDummyFile = dummyFile;
    }
}
