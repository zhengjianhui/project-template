package com.yzf.project.projectservice.config.redis.serializer;


import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;


/**
 * Hessian序列化开启GZIP支持
 *
 * @author qn
 * @since
 */
public class HessianSerializationRedisSerializer implements RedisSerializer<Object> {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializationRedisSerializer.class);

    private static final int MARK_INDEX = 2;

    private boolean enableGzip = false;

    final byte[] EMPTY_ARRAY = new byte[0];

    boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    /**
     * 反序列化
     */
    public Object deserialize(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = null;
        HessianInput hessianInput = null;
        GZIPInputStream gzipInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            if (isGzip(bytes)) {
                gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                bytes = new byte[1024];
                int length = -1;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                while ((length = gzipInputStream.read(bytes, 0, bytes.length)) != -1) {
                    byteArrayOutputStream.write(bytes, 0, length);
                }
                byteArrayOutputStream.close();
                bytes = byteArrayOutputStream.toByteArray();
            }
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (Exception e) {
            logger.error("deserialize:", e);
            throw new SerializationException("Cannot deserialize", e);
        } finally {
            try {
                if (gzipInputStream != null) {
                    gzipInputStream.close();
                }
                if (hessianInput != null) {
                    hessianInput.close();
                }
            } catch (Exception e) {
                logger.error("deserialize close", e);
            }
        }
    }

    private boolean isGzip(byte[] bytes) {
        if (bytes == null || bytes.length < MARK_INDEX) {
            return false;
        }
        int result = -1;
        byte[] header = new byte[2];
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            byteArrayInputStream.mark(MARK_INDEX);
            result = byteArrayInputStream.read(header);
            byteArrayInputStream.close();
        } catch (Exception e) {
            logger.error("isGzip:", e);
        }
        int gzipMagic = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
        if (result != -1 && gzipMagic == GZIPInputStream.GZIP_MAGIC) {
            return true;
        }
        return false;
    }

    /**
     * 序列化
     */
    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        HessianOutput hessianOutput = null;
        GZIPOutputStream gzipOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            if (enableGzip) {
                gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                hessianOutput = new HessianOutput(gzipOutputStream);
            } else {
                hessianOutput = new HessianOutput(byteArrayOutputStream);
            }
            hessianOutput.writeObject(object);
            if (gzipOutputStream != null) {
                gzipOutputStream.close();
            }
            byteArrayOutputStream.close();
            hessianOutput.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("serialize:", e);
            throw new SerializationException("Cannot serialize", e);
        } finally {
            try {
                if (gzipOutputStream != null) {
                    gzipOutputStream.close();
                }
                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
                if (hessianOutput != null) {
                    hessianOutput.close();
                }
            } catch (Exception e) {
                logger.error("serialize close:", e);
            }
        }
    }

    public boolean isEnableGzip() {
        return enableGzip;
    }

    public void setEnableGzip(boolean enableGzip) {
        this.enableGzip = enableGzip;
    }

}
