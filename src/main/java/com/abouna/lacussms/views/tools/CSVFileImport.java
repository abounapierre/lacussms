package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.dto.ClientCSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CSVFileImport {
    public static List<ClientCSV> parseFile(String path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            return lines.stream()
                    .skip(1) // Skip the header line
                    .map(line -> {
                        String[] fields = line.split(";");
                        return new ClientCSV(
                                1,
                                getValue(fields,0),
                                getValue(fields,1),
                                getValue(fields,2),
                                getValue(fields,3),
                                getValue(fields,4),
                                getValue(fields,5),
                                getValue(fields,6)
                        );
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getValue(String[] fields, int index) {
        try {
            return fields[index].trim();
        } catch (Exception e) {
            return "";
        }
    }
}
