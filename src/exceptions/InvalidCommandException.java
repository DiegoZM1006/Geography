package exceptions;

public class InvalidCommandException extends Exception{

    public InvalidCommandException(){
            super("THE ENTERED COMMAND IS INVALID");
    }
}
