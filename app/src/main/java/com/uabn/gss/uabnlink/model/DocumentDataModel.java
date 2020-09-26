package com.uabn.gss.uabnlink.model;

public class DocumentDataModel {

    String DocumentName;
    String DocumentPosted;
    String DocemntDetails;
    String DocumentIcon;
    String cat_id;
    String id;
    String Media_type;
    String website;

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getMedia_type() {
        return Media_type;
    }

    public void setMedia_type(String media_type) {
        Media_type = media_type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }



    public String getDocumentName() {
        return DocumentName;
    }
    public void setDocumentName(String wing) {
        DocumentName = wing;
    }

    public String getDocumentPosted() {
        return DocumentPosted;
    }
    public void setDocumentPosted(String wing) {
        DocumentPosted = wing;
    }

    public String getDocemntDetails() {
        return DocemntDetails;
    }
    public void setDocemntDetails(String wing) {
        DocemntDetails = wing;
    }

    public String getDocumentIcon() {
        return DocumentIcon;
    }
    public void setDocumentIcon(String wing) {
        DocumentIcon = wing;
    }

}

