package v.team.works.u22.hal.u22verification;

/**
 * Created by ohs60365 on 2018/07/25.
 */

public interface ImageUploadListener {
    abstract public void postCompletion(byte[] response);
    abstract public void postFialure();
}
