package com.ledongli.player.net;

import com.ledongli.player.utils.MyConstant;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 功能描述：
 * 打印请求信息
 * 源码okhttp3.logging.HttpLoggingInterceptor
 compile 'com.squareup.okhttp3:logging-interceptor:3.4.1'//日志
 * Created by zpp_zoe on 2017/11/1.
 */

public class MyLogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        return initResponse(chain);
    }

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private Response initResponse(Chain chain) throws IOException{
        if(MyConstant.isShowSysText)System.out.println("----------------------------【开始请求】----------------------------");
        boolean logHeaders = true;
        boolean logBody = true;

        Request request = chain.request();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        if(MyConstant.isShowSysText)System.out.println(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    if(MyConstant.isShowSysText)System.out.println("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    if(MyConstant.isShowSysText)System.out.println("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    if(MyConstant.isShowSysText)System.out.println(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                if(MyConstant.isShowSysText)System.out.println("--> END " + request.method());
            } else if (bodyEncoded(request.headers())) {
                if(MyConstant.isShowSysText)System.out.println("--> END " + request.method() + " (encoded body omitted)");
            } else {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);

                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }

                if(MyConstant.isShowSysText)System.out.println("");
                if (isPlaintext(buffer)) {
                    if(MyConstant.isShowSysText)System.out.println(buffer.readString(charset));
                    if(MyConstant.isShowSysText)System.out.println("--> END " + request.method()
                            + " (" + requestBody.contentLength() + "-byte body)");
                } else {
                    if(MyConstant.isShowSysText)System.out.println("--> END " + request.method() + " (binary "
                            + requestBody.contentLength() + "-byte body omitted)");
                }
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            if(MyConstant.isShowSysText)System.out.println("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);


        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        if(MyConstant.isShowSysText)System.out.println("<-- " + response.code() + ' ' + response.message() + ' '
                + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", "
                + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                if(MyConstant.isShowSysText)System.out.println(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                if(MyConstant.isShowSysText)System.out.println("<-- END HTTP");
            } else if (bodyEncoded(response.headers())) {
                if(MyConstant.isShowSysText)System.out.println("<-- END HTTP (encoded body omitted)");
            } else {
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                Buffer buffer = source.buffer();

                Charset charset = UTF8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8);
                    } catch (UnsupportedCharsetException e) {
                        if(MyConstant.isShowSysText)System.out.println("");
                        if(MyConstant.isShowSysText)System.out.println("Couldn't decode the response body; charset is likely malformed.");
                        if(MyConstant.isShowSysText)System.out.println("<-- END HTTP");

                        return response;
                    }
                }

                if (!isPlaintext(buffer)) {
                    if(MyConstant.isShowSysText)System.out.println("");
                    if(MyConstant.isShowSysText)System.out.println("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                    return response;
                }

                if (contentLength != 0) {
                    if(MyConstant.isShowSysText)System.out.println("");
                    if(MyConstant.isShowSysText)System.out.println(buffer.clone().readString(charset));
                }

                if(MyConstant.isShowSysText)System.out.println("<-- END HTTP (" + buffer.size() + "-byte body)");
            }
        }
        if(MyConstant.isShowSysText)System.out.println("-------------------【结束请求】----------------------------");
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}
