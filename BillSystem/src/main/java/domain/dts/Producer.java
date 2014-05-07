/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package domain.dts;

import road.billdts.connections.BillClient;

import javax.enterprise.inject.Produces;
import qualifier.ProducerQualifier;

/**
 *
 * @author Mitch
 */
public class Producer {
    @Produces @ProducerQualifier
    public BillClient billClientConnectionProducer(){
        return new BillClient();
    }
}
