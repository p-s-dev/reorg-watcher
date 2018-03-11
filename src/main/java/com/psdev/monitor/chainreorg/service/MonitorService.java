package com.psdev.monitor.chainreorg.service;

import com.psdev.monitor.chainreorg.model.AutoDiscardingDeque;
import com.psdev.monitor.chainreorg.model.SimpleBlock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.filters.FilterException;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class MonitorService {

    private static final int BLOCK_HISTORY = 100;

    @Autowired
    public Map<String, Web3j> rpcClients;
    public Map<String, Subscription> rpcSubscriptions;

    Map<String, AutoDiscardingDeque<SimpleBlock>> seenBlocks = new HashMap<>();


    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        rpcSubscriptions = new HashMap<>();

        for (Map.Entry<String,Web3j> entry : rpcClients.entrySet()) {
            String networkName = entry.getKey();
            log.info("subscribing to blocks for network=" + networkName);
            Web3j client = entry.getValue();
            try {
                seenBlocks.put(networkName, new AutoDiscardingDeque<SimpleBlock>(BLOCK_HISTORY));
                Subscription subscription = client.blockObservable(false).subscribe(b -> {
                    try {
                        SimpleBlock block = new SimpleBlock(b.getBlock().getNumber(), b.getBlock().getHash());
                        logBlock(networkName, block);
                        SimpleBlock lastBlock = seenBlocks.get(networkName).peek();
                        if (lastBlock != null &&
                                !lastBlock.getBlockNumber().add(BigInteger.ONE).equals(block.getBlockNumber())) {
                            // previous seen block is not same block number as current block.
                            // Blocks coming out of order?
                            log.warn("Blocks out of order.  network=" + networkName +
                                    " previous=" + lastBlock.toString() +
                                    " current=" + block.toString()
                            );
                        }
                        seenBlocks.get(networkName).offerFirst(block);
                    } catch (FilterException f) {
                        log.error("filter error " + f.getMessage());
                    } catch (Throwable t) {
                        log.error("throwable " + t.getMessage());
                    }
                });
                rpcSubscriptions.put(entry.getKey(), subscription);

            } catch (FilterException filterException) {
                log.error("error subscribing to block filter for network=" + entry.getKey());
            } catch (OnErrorNotImplementedException error) {
                log.error("error subscribing to block filter for network=" + entry.getKey());
            }
        }

    }

    public static synchronized void logBlock(String chain, SimpleBlock b) {
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.rightPad(chain, 10));
        sb.append(StringUtils.leftPad(b.getBlockNumber().toString(), 9));
        sb.append(StringUtils.leftPad(StringUtils.left(b.getBlockHash(), 7), 9));
        log.info(sb.toString());
    }

    @EventListener
    public void onApplicationEvent(ContextClosedEvent event) {

        for (Map.Entry<String,Subscription> entry : rpcSubscriptions.entrySet()) {
            Subscription subscription = entry.getValue();
            subscription.unsubscribe();
            log.info("unsubsubscribe: network=" + entry.getKey() + " success=" + subscription.isUnsubscribed());
        }
    }

}
