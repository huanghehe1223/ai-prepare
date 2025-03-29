package upc.projectname.projectservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import upc.projectname.upccommon.domain.po.Result;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * PDF代理服务控制器
 * 用于绕过前端跨域限制
 */
@Tag(name = "PDF代理接口")
@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
@Slf4j
public class PdfProxyController {

    private final RestTemplate restTemplate;

    @Operation(summary = "获取PDF内容", description = "接收PDF URL并返回PDF内容，绕过跨域限制")
    @GetMapping("/fetch")
    public ResponseEntity<?> fetchPdf(
            @Parameter(description = "PDF的URL", required = true) @RequestParam String pdfUrl,
            @Parameter(description = "响应格式: binary, base64, 或 stream", required = false)
            @RequestParam(required = false, defaultValue = "binary") String responseFormat) {

        log.info("Proxying PDF request for URL: {}, format: {}", pdfUrl, responseFormat);

        try {
            // 发送请求获取PDF内容
            ResponseEntity<byte[]> response = restTemplate.getForEntity(new URI(pdfUrl), byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                byte[] pdfData = response.getBody();

                // 根据请求的格式返回不同类型的响应
                switch (responseFormat.toLowerCase()) {
                    case "base64":
                        // 返回Base64编码的PDF数据
                        Map<String, Object> result = new HashMap<>();
                        result.put("data", Base64.getEncoder().encodeToString(pdfData));
                        result.put("contentType", "application/pdf");
                        result.put("filename", extractFilename(pdfUrl));
                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(result);

                    case "stream":
                        // 返回流式响应
                        StreamingResponseBody stream = outputStream -> {
                            try {
                                outputStream.write(pdfData);
                                outputStream.flush();
                            } catch (IOException e) {
                                log.error("Error writing PDF stream", e);
                            }
                        };

                        return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_PDF)
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + extractFilename(pdfUrl))
                                .body(stream);

                    case "binary":
                    default:
                        // 返回二进制PDF（默认行为）
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_PDF);
                        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + extractFilename(pdfUrl));
                        return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
                }
            } else {
                // 返回错误信息
                Map<String, Object> error = new HashMap<>();
                error.put("error", "无法获取PDF文件");
                error.put("statusCode", response.getStatusCodeValue());
                return ResponseEntity.status(response.getStatusCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(error);
            }

        } catch (Exception e) {
            log.error("Error proxying PDF: ", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "处理PDF请求时出错");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(error);
        }
    }

    @Operation(summary = "获取嵌入用的PDF URL", description = "返回一个可以直接嵌入iframe的URL")
    @GetMapping("/embed-url")
    public Result<Object> getEmbedUrl(
            @Parameter(description = "PDF的URL", required = true) @RequestParam String pdfUrl) {

        log.info("Generating embed URL for: {}", pdfUrl);

        try {
            Map<String, Object> result = new HashMap<>();
            // 构建本服务的代理URL，用于前端嵌入
            String embedUrl = "/pdf/fetch?pdfUrl=" + pdfUrl;
            result.put("embedUrl", embedUrl);
            result.put("originalUrl", pdfUrl);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Error generating embed URL: ", e);
            return Result.error("生成嵌入URL失败: " + e.getMessage());
        }
    }

    @Operation(summary = "PDF URL信息", description = "获取PDF URL的元数据信息")
    @GetMapping("/info")
    public Result<Object> getPdfInfo(
            @Parameter(description = "PDF的URL", required = true) @RequestParam String pdfUrl) {

        log.info("Getting PDF info for URL: {}", pdfUrl);

        try {
            // 使用exchange方法发送HEAD请求获取资源信息而不下载完整内容
            ResponseEntity<Void> response = restTemplate.exchange(
                    new URI(pdfUrl),
                    HttpMethod.HEAD,
                    null,
                    Void.class
            );

            Map<String, Object> result = new HashMap<>();
            result.put("accessible", true);
            result.put("statusCode", response.getStatusCodeValue());
            result.put("contentType", response.getHeaders().getContentType());
            result.put("contentLength", response.getHeaders().getContentLength());
            result.put("filename", extractFilename(pdfUrl));

            return Result.success(result);

        } catch (Exception e) {
            log.error("Error getting PDF info: ", e);
            Map<String, Object> result = new HashMap<>();
            result.put("accessible", false);
            result.put("error", e.getMessage());
            return Result.success(result);
        }
    }

    @Operation(summary = "重定向至PDF", description = "服务器端重定向到PDF URL")
    @GetMapping("/redirect")
    public ResponseEntity<Void> redirectToPdf(
            @Parameter(description = "PDF的URL", required = true) @RequestParam String pdfUrl) {

        log.info("Redirecting to PDF URL: {}", pdfUrl);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(pdfUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);

        } catch (Exception e) {
            log.error("Error redirecting to PDF: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 从URL中提取文件名
     */
    private String extractFilename(String url) {
        String filename = "document.pdf";
        if (url != null && !url.isEmpty()) {
            int lastSlashIndex = url.lastIndexOf('/');
            if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
                String nameWithParams = url.substring(lastSlashIndex + 1);
                int questionMarkIndex = nameWithParams.indexOf('?');
                if (questionMarkIndex > 0) {
                    return nameWithParams.substring(0, questionMarkIndex);
                }
                return nameWithParams;
            }
        }
        return filename;
    }

    @Operation(summary = "测试PDF代理服务", description = "测试PDF代理服务是否正常运行")
    @GetMapping("/test")
    public Result<Object> testPdfProxy() {
        Map<String, Object> testResult = new HashMap<>();
        testResult.put("status", "success");
        testResult.put("message", "PDF代理服务正常运行");
        testResult.put("timestamp", System.currentTimeMillis());

        return Result.success(testResult);
    }
}
