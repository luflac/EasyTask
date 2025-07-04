package com.kahlab.easytask.service;

import com.kahlab.easytask.DTO.TaskGeneralReportDTO;
import com.kahlab.easytask.DTO.TaskPriorityReportDTO;
import com.kahlab.easytask.repository.TaskRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import com.lowagie.text.Image;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class PdfReportService {

    private final TaskRepository taskRepository;

    public PdfReportService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public byte[] generatePriorityReportPdf() {
        List<TaskPriorityReportDTO> tasks = taskRepository.findAllTasksOrderedByPriority();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // ✅ LOGO CENTRALIZADA
            try {
                java.net.URL imageUrl = getClass().getResource("/logo-easytask.png");
                if (imageUrl != null) {
                    Image logo = Image.getInstance(imageUrl);
                    logo.scaleToFit(100, 100);
                    logo.setAlignment(Image.ALIGN_CENTER);
                    document.add(logo);
                }
            } catch (Exception e) {
                // Se der erro ao carregar a logo, apenas continua
                System.err.println("Não foi possível carregar a logo: " + e.getMessage());
            }

            // ✅ TÍTULO
            Paragraph title = new Paragraph("Relatório de Tarefas por Prioridade",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // ✅ DATA DE GERAÇÃO
            Paragraph data = new Paragraph("Emitido em: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            data.setAlignment(Element.ALIGN_CENTER);
            document.add(data);

            document.add(new Paragraph(" ")); // Espaço

            // ✅ TABELA DE TAREFAS
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addHeader(table, "Prioridade");
            addHeader(table, "Título");
            addHeader(table, "Data de Entrega");
            addHeader(table, "Fase");
            addHeader(table, "Cliente");
            addHeader(table, "Colaborador");

            for (TaskPriorityReportDTO task : tasks) {
                table.addCell(String.valueOf(task.getPriority()));
                table.addCell(task.getTitle());
                table.addCell(task.getDueDate() != null ? task.getDueDate().toString() : "—");
                table.addCell(task.getPhaseName());
                table.addCell(task.getClientName());
                table.addCell(task.getCollaboratorName());
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public byte[] generateGeneralReportPdf(String type, Long id) {
        List<TaskGeneralReportDTO> tasks;
        String personName;

        if ("client".equalsIgnoreCase(type)) {
            tasks = taskRepository.findTasksByClientId(id);
            personName = taskRepository.findClientNameById(id); // Nova query
        } else if ("collaborator".equalsIgnoreCase(type)) {
            tasks = taskRepository.findTasksByCollaboratorId(id);
            personName = taskRepository.findCollaboratorNameById(id); // Nova query
        } else {
            throw new IllegalArgumentException("Tipo inválido: use 'client' ou 'collaborator'");
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            String titleText = "Relatório de Tarefas por " + ("client".equalsIgnoreCase(type) ? "Cliente" : "Colaborador");
            Paragraph title = new Paragraph(titleText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Data
            Paragraph data = new Paragraph("Emitido em: " + LocalDate.now(),
                    FontFactory.getFont(FontFactory.HELVETICA, 12));
            data.setAlignment(Element.ALIGN_CENTER);
            document.add(data);

            // Nome
            Paragraph nome = new Paragraph("Referente a: " + personName,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
            nome.setAlignment(Element.ALIGN_CENTER);
            document.add(nome);


            document.add(new Paragraph(" "));

            // Tabela
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addHeader(table, "Título");
            addHeader(table, "Prioridade");
            addHeader(table, "Data de Entrega");
            addHeader(table, "Fase");
            addHeader(table, type.equals("client") ? "Colaborador" : "Cliente");

            for (TaskGeneralReportDTO task : tasks) {
                table.addCell(task.getTitle());
                table.addCell(String.valueOf(task.getPriority()));
                table.addCell(task.getDueDate() != null ? task.getDueDate().toString() : "—");
                table.addCell(task.getPhaseName());
                table.addCell(task.getOtherPartyName());
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do relatório geral", e);
        }
    }

    private void addHeader(PdfPTable table, String title) {
        PdfPCell cell = new PdfPCell(new Phrase(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }
}
