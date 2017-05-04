package pt.ulisboa.tecnico.meic.sec.lib.exception;

import pt.ulisboa.tecnico.meic.sec.lib.SecureEntity;

public class EntityAlreadyExistsException extends Exception {
    SecureEntity entity;

    public EntityAlreadyExistsException(SecureEntity entity){
        this.entity = entity;
    }

    public SecureEntity getEntity(){
        return entity;
    }
}
