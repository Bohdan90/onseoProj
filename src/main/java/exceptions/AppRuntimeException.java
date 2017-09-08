package exceptions;

/**
 * Created by bskrypka on 07.09.2017.
 */
public class AppRuntimeException extends RuntimeException {
    public AppRuntimeException(String msg) {
        super(msg);
    }
}
