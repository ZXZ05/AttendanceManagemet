package com.zpark.sb.controller;

import com.alibaba.excel.EasyExcel;
import com.zpark.sb.config.Result;
import com.zpark.sb.config.ResultCode;
import com.zpark.sb.entity.imports.ExcelImportResult;
import com.zpark.sb.service.AuthContextService;
import com.zpark.sb.service.ExcelImportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/excelImport")
public class ExcelImportController {

    @Autowired
    private AuthContextService authContextService;
    @Autowired
    private ExcelImportService excelImportService;

    @PostMapping(value = "/department", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importDepartment(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        return executeImport(request, authContextService.canManageOrg(request), () -> excelImportService.importDepartments(file));
    }

    @PostMapping(value = "/position", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importPosition(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        return executeImport(request, authContextService.canManageOrg(request), () -> excelImportService.importPositions(file));
    }

    @PostMapping(value = "/employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importEmployee(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        return executeImport(request, authContextService.canManageEmployee(request), () -> excelImportService.importEmployees(file));
    }

    @PostMapping(value = "/customer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importCustomer(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        return executeImport(request, authContextService.canAccessAdminPortal(request),
                () -> excelImportService.importCustomers(file, currentUserNumber));
    }

    @PostMapping(value = "/fixedasset", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importFixedasset(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
        String currentUserNumber = authContextService.getCurrentUserNumber(request);
        return executeImport(request, authContextService.canAccessAdminPortal(request),
                () -> excelImportService.importFixedassets(file, currentUserNumber));
    }

    @GetMapping("/template")
    public void downloadTemplate(@RequestParam String type, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!authContextService.canAccessAdminPortal(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        List<List<String>> headers = getTemplateHeaders(type);
        if (headers.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String fileName = "import-template-" + type;
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(headers)
                .sheet("template")
                .doWrite(Collections.emptyList());
    }

    private List<List<String>> getTemplateHeaders(String type) {
        if ("employee".equals(type)) {
            return List.of(
                    List.of("工号"),
                    List.of("姓名"),
                    List.of("性别"),
                    List.of("手机号"),
                    List.of("部门编号"),
                    List.of("岗位编号"),
                    List.of("生日(yyyy-MM-dd)"),
                    List.of("入职日期(yyyy-MM-dd)"),
                    List.of("身份证号"),
                    List.of("学历"),
                    List.of("婚姻"),
                    List.of("地址")
            );
        }
        if ("department".equals(type)) {
            return List.of(List.of("部门编号"), List.of("部门名称"));
        }
        if ("position".equals(type)) {
            return List.of(
                    List.of("岗位编号"),
                    List.of("岗位名称"),
                    List.of("月薪"),
                    List.of("部门编号"),
                    List.of("岗位类别编号")
            );
        }
        if ("customer".equals(type)) {
            return List.of(
                    List.of("客户编号"),
                    List.of("客户名称"),
                    List.of("类型"),
                    List.of("电话"),
                    List.of("地址"),
                    List.of("备注"),
                    List.of("归属人工号(可空)")
            );
        }
        if ("fixedasset".equals(type)) {
            return List.of(
                    List.of("资产编号"),
                    List.of("资产名称"),
                    List.of("资产类别编号"),
                    List.of("价格"),
                    List.of("归属人工号(可空)")
            );
        }
        return Collections.emptyList();
    }

    private Result executeImport(HttpServletRequest request, boolean allowed, ImportAction action) {
        if (!allowed) {
            return Result.failure(ResultCode.PERMISSION_NO_ACCESS);
        }
        try {
            ExcelImportResult result = action.importData();
            return Result.success(result);
        } catch (IllegalArgumentException ex) {
            Result result = Result.failure(ResultCode.PARAM_IS_INVALID);
            result.setMessage(ex.getMessage());
            return result;
        } catch (Exception ex) {
            Result result = Result.failure(ResultCode.SYSTEM_INNER_ERROR);
            result.setMessage("导入失败: " + ex.getMessage());
            return result;
        }
    }

    @FunctionalInterface
    private interface ImportAction {
        ExcelImportResult importData();
    }
}
