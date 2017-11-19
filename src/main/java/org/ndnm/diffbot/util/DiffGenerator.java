package org.ndnm.diffbot.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.Charsets;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

public class DiffGenerator {
    private List<String> originalFileLines;
    private List<String> revisedFileLines;
    private Patch patch;
    private List<Delta> allDeltas;
    private List<Delta> changeDeltas;
    private List<Delta> insertDeltas;
    private List<Delta> deleteDeltas;


    public DiffGenerator(File originalFileLines, File revisedFileLines) {
        this.originalFileLines = fileToLines(originalFileLines);
        this.revisedFileLines = fileToLines(revisedFileLines);
        init();
    }


    public DiffGenerator(List<String> originalFileLines, List<String> revisedFileLines) {
        this.originalFileLines = originalFileLines;
        this.revisedFileLines = revisedFileLines;
        init();
    }


    private void init() {
        this.changeDeltas = new ArrayList<>();
        this.insertDeltas = new ArrayList<>();
        this.deleteDeltas = new ArrayList<>();
        diff();
    }


    @SuppressWarnings("unchecked")//patch.getDeltas()
    private void diff() {
        patch = DiffUtils.diff(originalFileLines, revisedFileLines);
        allDeltas = patch.getDeltas();

        for (Delta delta : allDeltas) {
            if (delta.getType().equals(Delta.TYPE.CHANGE)) {
                this.changeDeltas.add(delta);
            } else if (delta.getType().equals(Delta.TYPE.INSERT)) {
                this.insertDeltas.add(delta);
            } else if (delta.getType().equals(Delta.TYPE.DELETE)) {
                this.deleteDeltas.add(delta);
            } else {
                throw new RuntimeException("Unknown delta type: " + delta.getType());
            }
        }
    }


    private List<String> fileToLines(File file) {
        try {
            return Files.readAllLines(file.toPath(), Charsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not read file!: " + e.getMessage());
        }
    }


    public List<String> getOriginalFileLines() {
        return originalFileLines;
    }


    public List<String> getRevisedFileLines() {
        return revisedFileLines;
    }


    public Patch getPatch() {
        return patch;
    }


    public List<Delta> getAllDeltas() {
        return allDeltas;
    }


    public List<Delta> getChangeDeltas() {
        return changeDeltas;
    }


    public List<Delta> getInsertDeltas() {
        return insertDeltas;
    }


    public List<Delta> getDeleteDeltas() {
        return deleteDeltas;
    }

}
