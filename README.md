# reorg-watcher

Code does this:

1. get web3 rpc connection to a bunch of eth-clone nodes
2. setup a filter to receive an event every time a new block is found
3. If a block number is not equal to the previous block number +1, then log a warning



open questions:
1. Are these uncle blocks?
2. Are these chain re-orgs?
3. Should PoA networks have zero duplicate blocks?
4. Isn't it true that *some* number of duplicate blocks is good, since it reflects healthy competion among a diverse group of PoW miners of relatively equal hashing speed.  But *too many* duplicate blocks is a sign of an unhealthy network.


See wiki for some sample log results: 
https://github.com/p-s-dev/reorg-watcher/wiki
