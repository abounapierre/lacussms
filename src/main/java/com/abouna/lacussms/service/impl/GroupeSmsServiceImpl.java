package com.abouna.lacussms.service.impl;

import com.abouna.generic.dao.DataAccessException;
import com.abouna.lacussms.dao.GroupeClientDao;
import com.abouna.lacussms.dao.GroupeDao;
import com.abouna.lacussms.dto.BkCliCompte;
import com.abouna.lacussms.dto.GroupeClientDTO;
import com.abouna.lacussms.dto.GroupeSmsRequestDTO;
import com.abouna.lacussms.dto.GroupeSmsResponseDTO;
import com.abouna.lacussms.entities.BkCli;
import com.abouna.lacussms.entities.BkCompCli;
import com.abouna.lacussms.entities.Groupe;
import com.abouna.lacussms.entities.GroupeClient;
import com.abouna.lacussms.entities.GroupeClientPK;
import com.abouna.lacussms.sender.context.SenderContext;
import com.abouna.lacussms.service.GroupeSmsService;
import com.abouna.lacussms.service.LacusSmsService;
import com.abouna.lacussms.service.impl.util.GroupeSmsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abouna.lacussms.views.tools.ConstantUtils.COMA;

@Service
@Transactional
public class GroupeSmsServiceImpl implements GroupeSmsService {
    private final GroupeClientDao groupeClientDao;
    private final GroupeDao groupeDao;
    private final LacusSmsService lacusSmsService;
    private final SenderContext senderContext;

    public GroupeSmsServiceImpl(GroupeClientDao groupeClientDao, GroupeDao groupeDao, LacusSmsService lacusSmsService, SenderContext senderContext) {
        this.groupeClientDao = groupeClientDao;
        this.groupeDao = groupeDao;
        this.lacusSmsService = lacusSmsService;
        this.senderContext = senderContext;
    }


    @Override
    public Groupe saveGroupe(Groupe groupe) {
        try {
            return groupeDao.create(groupe);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<Groupe> saveGroupeList(List<Groupe> groupes) {
        try {
            return groupes.stream().peek(
                    groupe -> {
                        try {
                            groupeDao.create(groupe);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group list", e);
        }
    }

    @Override
    public Groupe updateGroupe(Groupe groupe) {
        try {
            return groupeDao.update(groupe);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public Groupe findGroupeById(Integer id) {
        try {
            return groupeDao.findById(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public void deleteGroupeById(Integer id) {
        try {
            if(!groupeClientDao.findByGroupeId(id).isEmpty()) {
                groupeClientDao.deleteGroupeClientByGroupeId(id);
            }
             groupeDao.delete(groupeDao.findById(id));
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<Groupe> findAllGroupes() {
        try {
            return groupeDao.findAll();
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<GroupeClient> saveList(List<GroupeClient> groupeClients) {
        try {
            return groupeClients.stream().peek(
                    groupeClient -> {
                        try {
                            groupeClientDao.create(groupeClient);
                        } catch (DataAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ).collect(Collectors.toList());
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group list", e);
        }
    }

    @Override
    public GroupeClient findGroupeClientById(GroupeClientPK id) {
        try {
            return groupeClientDao.findById(id);
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public void deleteGroupeClientById(GroupeClientPK id) {
        try {
            groupeClientDao.delete(groupeClientDao.findById(id));
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while deleteGroupeClientById", e);
        }
    }

    @Override
    public List<GroupeClient> findAllGroupeClients() {
        try {
            return groupeClientDao.findAll();
        } catch (DataAccessException | RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<BkCli> findClientsByGroupe(Integer id) {
        return groupeClientDao.findByGroupeId(id).stream().map(GroupeClient::getId).map(GroupeClientPK::getIdClient)
                .map(lacusSmsService::getBkCliById).collect(Collectors.toList());
    }

    @Override
    public List<BkCliCompte> findClientsByGroupeId(Integer id) {
        return groupeClientDao.findByGroupeId(id).stream().map(GroupeClient::getId).map(GroupeClientPK::getIdClient)
                .map(lacusSmsService::getBkCliById).map(bkCli -> {
                    BkCliCompte bkCliCompte = new BkCliCompte();
                    bkCliCompte.setBkCli(bkCli);
                    Optional<BkCompCli> bkCliCompteOpt = lacusSmsService.getBkCompCliByCli(bkCli).stream().findFirst();
                    bkCliCompte.setCompte(bkCliCompteOpt.map(BkCompCli::getNumc).orElse(""));
                    return bkCliCompte;
                }).collect(Collectors.toList());
    }

    @Override
    public List<BkCliCompte> findClientsNotInGroupe(Integer id) {
        List<BkCli> clients = groupeClientDao.findByGroupeId(id).stream().map(GroupeClient::getId).map(GroupeClientPK::getIdClient)
                .map(lacusSmsService::getBkCliById).collect(Collectors.toList());
        List<BkCli> allClients = lacusSmsService.getAllCli();
        List<BkCli> clientsNotInGroupe = allClients.stream().filter(bkCli -> !clients.contains(bkCli)).collect(Collectors.toList());
        if (!clientsNotInGroupe.isEmpty()) {
            return clientsNotInGroupe.stream().map(bkCli -> {
                BkCliCompte bkCliCompte = new BkCliCompte();
                bkCliCompte.setBkCli(bkCli);
                Optional<BkCompCli> bkCliCompteOpt = lacusSmsService.getBkCompCliByCli(bkCli).stream().findFirst();
                bkCliCompte.setCompte(bkCliCompteOpt.map(BkCompCli::getNumc).orElse(""));
                return bkCliCompte;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public void addClientsToGroupe(List<String> selectedClients, Groupe groupe) {
        try {
            List<GroupeClient> groupeClients = selectedClients.stream().map(clientId -> {
                GroupeClientPK groupeClientPK = new GroupeClientPK();
                groupeClientPK.setIdGroupe(groupe.getId());
                groupeClientPK.setIdClient(clientId);
                GroupeClient groupeClient = new GroupeClient();
                groupeClient.setId(groupeClientPK);
                return groupeClient;
            }).collect(Collectors.toList());
            saveList(groupeClients);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public int removeClientsFromGroupe(Groupe groupe, List<String> selectedClients) {
        try {
            List<GroupeClient> groupeClients = selectedClients.stream().map(clientId -> {
                GroupeClientPK groupeClientPK = new GroupeClientPK();
                groupeClientPK.setIdGroupe(groupe.getId());
                groupeClientPK.setIdClient(clientId);
                try {
                    return groupeClientDao.findById(groupeClientPK);
                } catch (DataAccessException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            if (!groupeClients.isEmpty()) {
                groupeClients.forEach(groupeClient -> {
                    try {
                        groupeClientDao.delete(groupeClient);
                    } catch (DataAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            return groupeClients.size();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while saving group", e);
        }
    }

    @Override
    public List<BkCliCompte> findClientsCompte() {
        List<BkCli> allClients = lacusSmsService.getAllCli();
        if (!allClients.isEmpty()) {
            return allClients.stream().map(bkCli -> {
                BkCliCompte bkCliCompte = new BkCliCompte();
                bkCliCompte.setBkCli(bkCli);
                Optional<BkCompCli> bkCliCompteOpt = lacusSmsService.getBkCompCliByCli(bkCli).stream().findFirst();
                bkCliCompte.setCompte(bkCliCompteOpt.map(BkCompCli::getNumc).orElse(""));
                return bkCliCompte;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public GroupeSmsResponseDTO sendSmsGroupe(GroupeSmsRequestDTO requestDTO) {
       try {
           GroupeSmsUtil.validateGroupeSmsRequest(requestDTO);
           if(requestDTO.getPhoneNumbers() != null && !requestDTO.getPhoneNumbers().isEmpty()) {
               GroupeSmsUtil.sendWithPhoneNumbers(senderContext, requestDTO.getMessage(), requestDTO.getPhoneNumbers());
           }
           if(requestDTO.getGroupes() != null && !requestDTO.getGroupes().isEmpty()) {
               GroupeSmsUtil.sendWithGroupIds(senderContext, requestDTO.getMessage(), mapGroupeIdsToBkClis(requestDTO.getGroupes()), requestDTO.isPersonalized());
           }
           if(requestDTO.getClients() != null && !requestDTO.getClients().isEmpty()) {
            GroupeSmsUtil.sendWithClientIds(senderContext, requestDTO.getMessage(), mapClientIdsToBkClis(requestDTO.getClients()), requestDTO.isPersonalized());
           }
           return new GroupeSmsResponseDTO();
       } catch (RuntimeException e) {
           throw new RuntimeException("Error while saving group: " + e.getMessage(), e);
       }
    }

    private List<BkCli> mapClientIdsToBkClis(String clients) {
        return Stream.of(clients.split(COMA)).map(lacusSmsService::getBkCliById).collect(Collectors.toList());
    }

    private List<GroupeClientDTO> mapGroupeIdsToBkClis(String groupes) {
        return Stream.of(groupes.split(COMA))
                .map(groupe -> new GroupeClientDTO(Integer.parseInt(groupe), findClientsByGroupe(Integer.parseInt(groupe))))
                .collect(Collectors.toList());
    }
}
