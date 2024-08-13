/**
 * Copyright (C) Oceancode Cloud. 2024-2024 .All Rights Reserved.
 */

package com.oceancode.cloud.common.web.util;

import com.oceancode.cloud.common.constant.CommonConst;
import com.oceancode.cloud.common.entity.PartFile;
import com.oceancode.cloud.common.entity.ResultData;
import com.oceancode.cloud.common.entity.TextFileContent;
import com.oceancode.cloud.common.errorcode.CommonErrorCode;
import com.oceancode.cloud.common.exception.BusinessRuntimeException;
import com.oceancode.cloud.common.util.SessionUtil;
import com.oceancode.cloud.common.util.ValueUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <B>This is api util</B>
 *
 * <p>
 * This class is a util.
 * </p>
 *
 * @author Dynamic Gen
 * @since 1.0
 */
public final class ApiUtil {
    private ApiUtil() {
    }

    /**
     * set business fields to session from request.
     * <p>
     *     <ol>
     *         <li>x-project-id ProjectId</li>
     *         <li>x-tenant-id TenantId</li>
     *     </ol>
     * </p>
     *
     * @param request HttpServletRequest
     */
    public static void processCommonArguments(HttpServletRequest request) {
        String projectId = request.getHeader(CommonConst.X_PROJECT_ID);
        if (ValueUtil.isNotEmpty(projectId)) {
            SessionUtil.setProjectId(Long.parseLong(projectId));
            MDC.put(CommonConst.PROJECT_ID, projectId);
        }
        String tenantId = request.getHeader(CommonConst.X_TENANT_ID);
        if (ValueUtil.isNotEmpty(tenantId)) {
            SessionUtil.setTenantId(Long.parseLong(tenantId));
            MDC.put(CommonConst.TENANT_ID, tenantId);
        }
    }

    /**
     * A callback method witch user must not be login
     *
     * @param supplier callback method
     * @param <T>      datatype
     * @return ResultData<T>
     */
    public static <T> ResultData<T> doCallMethod(Supplier<ResultData<T>> supplier) {
        return supplier.get();
    }

    /**
     * Get HttpServletRequest
     *
     * @return {@link HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getResponse();
    }


    /**
     * check session
     *
     * @param tmpResult ResultData<?>
     * @param userId    session userId
     * @param projectId session projectId
     * @param tenantId  session tenantId
     */
    public static void checkSessionsValue(ResultData<?> tmpResult, boolean userId, boolean projectId, boolean tenantId) {
        if (userId && ValueUtil.isEmpty(SessionUtil.userId())) {
            tmpResult.code(CommonErrorCode.NOT_LOGIN);
            return;
        }
        if (projectId && ValueUtil.isEmpty(SessionUtil.projectId())) {
            tmpResult.code(CommonErrorCode.PARAMETER_MISSING).message("projectId missing");
            return;
        }
        if (tenantId && ValueUtil.isEmpty(SessionUtil.tenantId())) {
            tmpResult.code(CommonErrorCode.PARAMETER_MISSING).message("tenantId missing");
            return;
        }
    }

    public static String getAuthorizationToken() {
        return getToken(getRequest().getHeader("Authorization"));
    }

    public static String getToken(String authorization) {
        if (ValueUtil.isEmpty(authorization)) {
            return null;
        }
        String token = authorization.trim();
        if (token.startsWith("Bearer ")) {
            return token.substring("Bearer ".length()).trim();
        }
        throw new BusinessRuntimeException(CommonErrorCode.AUTHORIZATION_INVALID);
    }

    public static String getToken() {
        return getAuthorizationToken();
    }

    private static String getContentTypeByFileType(String fileType) {
        if ("xlsx".equalsIgnoreCase(fileType)) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if ("html".equalsIgnoreCase(fileType)) {
            return " Content-Type: text/html;charset:utf-8";
        }

        return null;
    }

    public static void initDownloadResponse(HttpServletResponse response, String fileName) {
        if (ValueUtil.isEmpty(fileName)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "fileName must not be empty.");
        }
        initDownloadResponse(response, fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".") + 1) : null, fileName);
    }

    public static void initDownloadResponse(HttpServletResponse response, String fileType, String fileName) {
        if (ValueUtil.isEmpty(fileName)) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, "fileName must not be empty.");
        }
        String contentType = getContentTypeByFileType(fileType);

        if (ValueUtil.isNotEmpty(contentType)) {
            response.setContentType(contentType);
        }
        response.setCharacterEncoding("utf-8");
        String exportFilename = null;
        try {
            exportFilename = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
        response.setHeader("Content-disposition", "attachment;filename=" + exportFilename);
    }


    public static void setCookie(String key, String value) {
        getResponse().addCookie(new Cookie(key, value));
    }

    public static void setTokenInCookie(String token) {
        setCookie("token", token);
    }

    public static String getTokenFromCookie() {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static String getHost() {
        return getRequest().getHeader("Host");
    }

    public static String getUserAgent() {
        return getRequest().getHeader("User-Agent");
    }

    public static String getHeader(String key) {
        return getRequest().getHeader(key);
    }

    public static void setSession(String key, Object value) {
        getRequest().getSession().setAttribute(key, value);
    }

    public static Object getSession(String key) {
        if (key == null) {
            return null;
        }
        return getRequest().getSession().getAttribute(key);
    }

    public static void removeSession(String... keys) {
        for (String key : keys) {
            getRequest().getSession().removeAttribute(key);
        }
    }

    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {

                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()= 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        // ipAddress = this.getRequest().getRemoteAddr();
        return ipAddress;
    }

    public static void exportZip(String zipName, List<TextFileContent> fileList) {
        HttpServletResponse response = getResponse();
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            exportZip(fileList, outputStream);
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipName);
            response.setCharacterEncoding("UTF-8");
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR);
        }
    }

    private static <T extends OutputStream> void exportZip(List<TextFileContent> fileList, T outputStream) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (TextFileContent compressFile : fileList) {
                ZipEntry zipEntry = new ZipEntry(compressFile.getFilename());
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(compressFile.getContent());
            }
        } catch (IOException e) {
            throw new BusinessRuntimeException(CommonErrorCode.SERVER_ERROR, e);
        }
    }
}