/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author SylvainPro
 */
public enum Statut {
    
    OK              (200,"OK"),
    NO_RESULT       (200,"NO RESULT"),
    BAD_REQUEST     (400,"BAD REQUEST"),
    NOT_FOUND       (404,"NOT FOUND OR UNVAILABLE"),
    TIME_OUT        (500,"TIME OUT"),
    SERVER_ERROR    (500,"SERVER ERROR");
        
    private Statut (Integer code, String message) {
        this.code = code;
        this.message = message;
    }
        
    private final Integer code;
    private final String message;
    
    public Integer getCode () {
        return this.code;
    }

    public String getMessage() {
        return message;
    }
        
}
