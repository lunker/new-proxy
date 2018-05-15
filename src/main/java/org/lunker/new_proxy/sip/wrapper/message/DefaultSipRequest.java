package org.lunker.new_proxy.sip.wrapper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;

import javax.sip.address.URI;
import javax.sip.header.ContentTypeHeader;

/**
 * Created by dongqlee on 2018. 4. 28..
 */
public class DefaultSipRequest extends DefaultSipMessage {

    public DefaultSipRequest(SIPMessage sipMessage) {
        super(sipMessage);
    }

    public void setContent(Object content, String contentType) {
//        this.checkContentType(contentType);

        try{
            ContentTypeHeader contentTypeHeader=null;
            contentTypeHeader=(ContentTypeHeader)this.message.getHeader("Content-Type");
            contentTypeHeader.setContentType("application");
            contentTypeHeader.setContentSubType("sdp");

            this.message.setContent(content, contentTypeHeader);
        }
        catch (Exception e){
            e.printStackTrace();
        }

//
//
//        if (contentType != null && contentType.length() > 0) {
//            this.addHeader("Content-Type", contentType);
//            ContentTypeHeader contentTypeHeader = (ContentTypeHeader)this.message.getHeader("Content-Type");
//            String charset = this.getCharacterEncoding();
//
//            try {
//                if (contentType.contains("multipart") && content instanceof Multipart) {
//                    Multipart multipart = (Multipart)content;
//                    OutputStream os = new ByteArrayOutputStream();
//                    multipart.writeTo(os);
//                    this.message.setContent(os.toString(), contentTypeHeader);
//                } else {
//                    if (content instanceof String && charset != null) {
//                        new String("testEncoding".getBytes(charset));
//                        new String(((String)content).getBytes());
//                    }
//
//                    this.message.setContent(content, contentTypeHeader);
//                }
//            } catch (UnsupportedEncodingException var7) {
//                throw var7;
//            } catch (Exception var8) {
//                throw new IllegalArgumentException("Parse error reading content " + content + " with content type " + contentType, var8);
//            }
//        }

    }

    private void checkContentType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("the content type cannot be null");
        } else {
            int indexOfSlash = contentType.indexOf("/");
            if (indexOfSlash != -1) {
                /*
                if (!JainSipUtils.IANA_ALLOWED_CONTENT_TYPES.contains(contentType.substring(0, indexOfSlash))) {
                    throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
                }
                */
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            } else {
                throw new IllegalArgumentException("the given content type " + contentType + " is not allowed");
            }

        }
    }

    public String getCharacterEncoding() {
        if (this.message.getContentEncoding() != null) {
            return this.message.getContentEncoding().getEncoding();
        } else {
            ContentTypeHeader cth = (ContentTypeHeader)this.message.getHeader("Content-Type");
            return cth == null ? null : cth.getParameter("charset");
        }
    }

    public Object getContent(){
        return this.message.getContent();
    }

    public URI getRequestURI(){
        SIPRequest sipRequest=(SIPRequest) this.message;
        return sipRequest.getRequestURI();
    }

    public void setRequestURI(URI requestURI){
        SIPRequest sipRequest=(SIPRequest) this.message;
        sipRequest.setRequestURI(requestURI);
    }
}
