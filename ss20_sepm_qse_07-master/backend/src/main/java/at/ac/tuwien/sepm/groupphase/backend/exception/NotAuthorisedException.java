package at.ac.tuwien.sepm.groupphase.backend.exception;

public class NotAuthorisedException extends RuntimeException{
    public NotAuthorisedException(String message){
       super(message);
    }
}
