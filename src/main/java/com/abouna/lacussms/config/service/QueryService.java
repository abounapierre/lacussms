/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abouna.lacussms.config.service;

import com.abouna.lacussms.entities.*;

import java.util.Map;

/**
 *
 * @author SATELLITE
 */
public class QueryService {

    public static String buildQuery(String query,Map<String,String> map,TypeService service) {
        if(map.isEmpty()){
            return null;
        }
        if(service.equals(TypeService.EVENEMENT)){
            return query
                .replace("NCP1", map.get(RequeteEvenement.NUMERO_COMPTE.name()))
                .replace("AGE", map.get(RequeteEvenement.CODE_AGENCE.name()))
                .replace("CLI1", map.get(RequeteEvenement.CODE_CLIENT.name()))
                .replace("ETA", map.get(RequeteEvenement.CODE_ETAT.name()))
                .replace("EVE", map.get(RequeteEvenement.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteEvenement.CODE_OPERATION.name()))
                .replace("DSAI", map.get(RequeteEvenement.DATE_EVENEMENT.name()))
                .replace("DVAB", map.get(RequeteEvenement.DATE_VALEUR.name()))
                .replace("HSAI", map.get(RequeteEvenement.HEURE_EVENEMENT.name()))
                .replace("BKEVE", map.get(RequeteEvenement.NOM_TABLE.name()))
                .replace("MON1", map.get(RequeteEvenement.MONTANT_OPERATION.name()));
        } else if(service.equals(TypeService.CREDIT)){
            return query
                .replace("NCP", map.get(RequeteCredit.NUMERO_COMPTE.name()))
                .replace("CAI", map.get(RequeteCredit.CODE_AI.name()))
                .replace("AGE", map.get(RequeteCredit.CODE_AGENCE.name()))
                .replace("ETA", map.get(RequeteCredit.CODE_ETAT.name()))
                .replace("EVE", map.get(RequeteCredit.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteCredit.CODE_OPERATION.name()))
                .replace("HEU", map.get(RequeteCredit.HEURE_EVENEMENT.name()))
                .replace("BKMAC", map.get(RequeteCredit.NOM_TABLE.name()))
                .replace("MNT", map.get(RequeteCredit.MONTANT_OPERATION.name()));
        } else if(service.equals(TypeService.HISTORIQUE)){
            return query
                .replace("NCP", map.get(RequeteHistorique.NUMERO_COMPTE.name()))
                .replace("AGE", map.get(RequeteHistorique.CODE_AGENCE.name()))
                .replace("EVE", map.get(RequeteHistorique.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteHistorique.CODE_OPERATION.name()))
                .replace("DVA", map.get(RequeteHistorique.DATE_VALEUR.name()))
                .replace("BKHIS", map.get(RequeteHistorique.NOM_TABLE.name()))
                .replace("MCTV", map.get(RequeteHistorique.MONTANT_OPERATION.name()));
        } else if(service.equals(TypeService.MANDAT)){
            return query
                .replace("CLESEC", map.get(RequeteMandat.CODE_OPERATION.name()))
                .replace("AGE", map.get(RequeteMandat.CODE_AGENCE.name()))
                .replace("CTR", map.get(RequeteMandat.CODE_CONFIRMATION.name()))
                .replace("DCO", map.get(RequeteMandat.DATE_COMPTABLE.name()))
                .replace("EVE", map.get(RequeteMandat.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteMandat.CODE_OPERATION.name()))
                .replace("DBD", map.get(RequeteMandat.DATE_EVENEMENT.name()))
                .replace("AD1P", map.get(RequeteMandat.DATE_ENVOIE.name()))
                .replace("AD2P", map.get(RequeteMandat.DATE_RETRAIT.name()))
                .replace("BKMAD", map.get(RequeteMandat.NOM_TABLE.name()))
                .replace("MNT", map.get(RequeteMandat.MONTANT_OPERATION.name()));
        }else if(service.equals(TypeService.SALAIRE1)){
            return query
                .replace("NCP", map.get(RequeteSalaire1.NUMERO_COMPTE.name()))
                .replace("AGE", map.get(RequeteSalaire1.CODE_AGENCE.name()))
                .replace("EVE", map.get(RequeteSalaire1.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteSalaire1.CODE_OPERATION.name()))
                .replace("DCO", map.get(RequeteSalaire1.DATE_COMPTABLE.name()))
                .replace("BKMPAI", map.get(RequeteSalaire1.NOM_TABLE.name()))
                .replace("MON", map.get(RequeteSalaire1.MONTANT_OPERATION.name()));
        }else if(service.equals(TypeService.SALAIRE2)){
            return query
                .replace("NCP", map.get(RequeteSalaire2.NUMERO_COMPTE.name()))
                .replace("AGE", map.get(RequeteSalaire2.CODE_AGENCE.name()))
                .replace("EVE", map.get(RequeteSalaire2.CODE_EVENEMENT.name()))
                .replace("OPE", map.get(RequeteSalaire2.CODE_OPERATION.name()))
                .replace("DVA", map.get(RequeteSalaire2.DATE_VALEUR.name()))
                .replace("DCO", map.get(RequeteSalaire2.DATE_COMPTABLE.name()))
                .replace("BKMVTI", map.get(RequeteSalaire2.NOM_TABLE.name()))
                .replace("MON", map.get(RequeteSalaire2.MONTANT_OPERATION.name()));
        }else if(service.equals(TypeService.SOLDE)){
            return query
                .replace("NCP", map.get(RequeteSolde.NUMERO_COMPTE.name()))
                .replace("SIN", map.get(RequeteSolde.SOLDE.name()))
                .replace("BKCOM", map.get(RequeteSolde.NOM_TABLE.name()));
        } else if(service.equals(TypeService.TELEPHONE_CLIENT)){
            return query
                .replace("NUM", map.get(RequeteClient.NUMERO_TELEPHONE.name()))
                .replace("CLI", map.get(RequeteClient.CODE_CLIENT.name()))
                .replace("TYP", map.get(RequeteClient.TYPE_CLIENT.name()))
                .replace("BKTELCLI", map.get(RequeteClient.NOM_TABLE.name()));
        } else if(service.equals(TypeService.AGENCE)){
            return query
                .replace("NCP", map.get(RequeteAgence.NUMERO_COMPTE.name()))
                .replace("AGE", map.get(RequeteAgence.CODE_AGENCE.name()))
                .replace("BKCOM", map.get(RequeteAgence.NOM_TABLE.name()));
        }
        return null;
    }
}
