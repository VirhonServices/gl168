package com.virhon.fintech.gl.repo;

public interface TransferRepo {
    Long reg(String uuid, String debitPageUuid, String creditPageUuid);
    TransferPages get(String uuid);
    TransferPages get(Long id);
}
