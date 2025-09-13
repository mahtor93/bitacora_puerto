package com.tornerias.bitacora.excel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelHandlerCartilla {

    /**
     * 🔹 Carga una plantilla existente desde assets y escribe valores.
     * Conserva los estilos, bordes y formatos de la plantilla.
     */
    /*
    public static void rellenarPlantilla(
            Context context,
            String plantillaAsset,
            String nombreArchivo,
            Integer registroNumero,
            Integer elementoNumero,
            Date fechaSupervision,
            String pinturaEsquema,
            Integer diametro

    ) {
        try (InputStream is = context.getAssets().open(plantillaAsset);
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 👉 Escribir ejemplo en celda AD5
            Row row5 = sheet.getRow(4);
            if (row5 == null) row5 = sheet.createRow(4);
            Cell cellAD5 = row5.getCell(29);
            if (cellAD5 == null) cellAD5 = row5.createCell(29);
            cellAD5.setCellValue(registroNumero);

            // 👉 Escribir ejemplo en celda I8
            Row rowI8 = sheet.getRow(7);
            if (rowI8 == null) rowI8 = sheet.createRow(7);
            Cell cellI8 = rowI8.getCell(8);
            if (cellI8 == null) cellI8 = rowI8.createCell(8);
            cellI8.setCellValue(elementoNumero);

            // 👉 Escribir fecha en AD6 con formato dd/MM/yyyy
            Row rowAD6 = sheet.getRow(5);
            if (rowAD6 == null) rowAD6 = sheet.createRow(5);
            Cell cellAD6 = rowAD6.getCell(29);
            if (cellAD6 == null) cellAD6 = rowAD6.createCell(29);
            cellAD6.setCellValue(new SimpleDateFormat("dd/MM/yyyy").format(fechaSupervision));

            //pinturaEsquema "verde" o "rojo" en Q9
            Row rowQ9 = sheet.getRow(8);
            if(rowQ9 == null) rowQ9 = sheet.createRow(8);
            Cell cellQ9 = rowQ9.getCell(16);
            if(cellQ9 == null) cellQ9 = rowQ9.createCell(16);
            if(pinturaEsquema != null){
                cellQ9.setCellValue(pinturaEsquema);
            } else {
                cellQ9.setCellValue("");
            }

            // diametro J14
            Row rowJ14 = sheet.getRow(13);
            if(rowJ14 == null) rowJ14 = sheet.createRow(13);
            Cell cellJ14 = rowJ14.getCell(9);
            if(cellJ14==null) cellJ14 = rowJ14.createCell(9);
            if (diametro != null) {
                cellJ14.setCellValue(diametro);
            } else {
                cellJ14.setCellValue("");
            }



            // Guardar archivo
            guardar(workbook, nombreArchivo);

        } catch (Exception e) {
            Log.e("ExcelHandlerCartilla", "❌ Error rellenando plantilla", e);
        }
    }
    */

    public static void rellenarPlantilla(
            Context context,
            String plantillaAsset,
            String nombreArchivo,
            Integer registroNumero,
            Integer elementoNumero,
            Date fechaSupervision,
            String pinturaBase,
            Integer diametro,
            char esquemaPilote,
            short rollos,
            String buzoAplicador1,
            String buzoAplicador2,
            String buzoSupervisor,
            String hidrodinamicaSupervisor,
            Date fechaSupervisionHidro
    ) {
        try (InputStream is = context.getAssets().open(plantillaAsset);
             XSSFWorkbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            CellStyle redCellStyle = createRedCellStyle(workbook);

            // 👉 Registro número en AD5
            setCellValue(sheet, 4, 29, registroNumero);

            // 👉 Elemento número en I8
            setCellValue(sheet, 7, 8, elementoNumero);

            // 👉 Fecha en AD6 con formato dd/MM/yyyy
            setCellValue(sheet, 5, 29, new SimpleDateFormat("dd/MM/yyyy").format(fechaSupervision));

            // 👉 Pintura base "Verde" o "Rojo" en R14
            setCellValue(sheet, 13, 17, pinturaBase);

            // 👉 Diámetro en J14
            setCellValue(sheet, 13, 9, diametro);

            // 👉 Esquema Pilote (A, B, D) con coloreado
            handleEsquemaPilote(sheet, redCellStyle, esquemaPilote);

            // 👉 Rollos en Y24
            setCellValue(sheet, 23, 24, rollos);

            // 👉 Buzo Aplicador 1 en I44
            setCellValue(sheet, 43, 8, buzoAplicador1);

            // 👉 Buzo Aplicador 2 en I44 (misma celda? ¿o debería ser otra?)
            // Asumo que es otra celda, si es la misma necesitarías concatenar
            setCellValue(sheet, 44, 8, buzoAplicador2); // ⚠️ Revisar si es otra celda

            // 👉 Buzo Supervisor en AA44
            setCellValue(sheet, 43, 26, buzoSupervisor);

            // 👉 Hidrodinámica Supervisor en N51
            setCellValue(sheet, 50, 13, hidrodinamicaSupervisor);

            // 👉 Fecha Supervisión Hidro en N52
            setCellValue(sheet, 51, 13, new SimpleDateFormat("dd/MM/yyyy").format(fechaSupervisionHidro));

            // Guardar archivo
            guardar(workbook, nombreArchivo);

        } catch (Exception e) {
            Log.e("ExcelHandlerCartilla", "❌ Error rellenando plantilla", e);
        }
    }

    // Método auxiliar para setear valores en celdas
    private static void setCellValue(Sheet sheet, int rowNum, int colNum, Object value) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof Date) {
                cell.setCellValue((Date) value);
            } else if (value instanceof String) {
                cell.setCellValue((String) value);
            } else {
                cell.setCellValue(value.toString());
            }
        } else {
            cell.setCellValue(""); // Celda vacía si es null
        }
    }

    // Método para manejar el esquema de pilote con coloreado
    private static void handleEsquemaPilote(Sheet sheet, CellStyle redCellStyle, char esquema) {
        // Limpiar celdas anteriores (opcional, para evitar múltiples selecciones)
        /*
        clearCellStyle(sheet, 15, 2); // C16
        clearCellStyle(sheet, 20, 3); // D21
        clearCellStyle(sheet, 15, 6); // G16
        */
        switch (esquema) {
            case 'A':
                setCellValueWithStyle(sheet, 15, 2, "A", redCellStyle); // C16
                break;
            case 'B':
                setCellValueWithStyle(sheet, 20, 3, "B", redCellStyle); // D21
                break;
            case 'D':
                setCellValueWithStyle(sheet, 15, 6, "D", redCellStyle); // G16
                break;
            default:
                Log.w("ExcelHandler", "Esquema de pilote no reconocido: " + esquema);
                break;
        }
    }

    // Método para setear valor con estilo
    private static void setCellValueWithStyle(Sheet sheet, int rowNum, int colNum, Object value, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }

        cell.setCellStyle(style);
        if (value != null) {
            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof String) {
                cell.setCellValue((String) value);
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    // Método para crear estilo de celda roja
    private static CellStyle createRedCellStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();

        // Usa el color rojo predefinido de Excel
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    // Método para limpiar estilo de celda (opcional)
    private static void clearCellStyle(Sheet sheet, int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            Cell cell = row.getCell(colNum);
            if (cell != null) {
                cell.setCellStyle(null);
                cell.setCellValue("");
            }
        }
    }

    /**
     * 🔹 Guarda el archivo Excel en /Documentos/bitacora
     */
    private static void guardar(Workbook workbook, String nombreArchivo) throws Exception {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "bitacora");
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            Log.d("ExcelHandlerCartilla", "📂 Carpeta creada: " + created);
        }

        File outFile = new File(folder, nombreArchivo);
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            workbook.write(fos);
        }
        workbook.close();

        Log.d("ExcelHandlerCartilla", "✅ Archivo exportado: " + outFile.getAbsolutePath());
    }
}
