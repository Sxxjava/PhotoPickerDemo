package org.itsk.photopickerdemo;

/**
 * Created by Jour on 2016/5/11.
 */
public class SelectedPicture {
    private String path;
    private boolean isShowDelete;

    public SelectedPicture(String path, boolean isShowDelete) {
        this.path = path;
        this.isShowDelete = isShowDelete;
    }

    public SelectedPicture() {
    }

    public boolean isShowDelete() {
        return isShowDelete;
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectedPicture picture = (SelectedPicture) o;

        return path != null ? path.equals(picture.path) : picture.path == null;

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (isShowDelete ? 1 : 0);
        return result;
    }
}
