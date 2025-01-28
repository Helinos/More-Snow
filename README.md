# More Snow

Adds more snow to BTA. 
Inspired by [Snow! Real Magic!](https://github.com/Snownee/SnowRealMagic/tree/1.20-fabric).

### Snow can now:
- Occupy the same block as foliage
- Accurately land on top of partial blocks such as stairs and slabs.
- Land around wooden fences, chainlink fences (TODO) and paper walls (TODO).
- ~~Propagate under overhands.~~\
    Originally this mod was going to replicate the snow accumulations behavior from [TerraFirmaCraft](https://github.com/TerraFirmaCraft/TerraFirmaCraft) where, when checking to see if snow should accumulate on this block, it would look at it's neighboring blocks and see if it could instead accumulate snow there if it had no/less snow that the original block. This was recursive and would've allowed snow to "creep" under trees and roofs. If there's any demand for this at all I'll probably add it back as an optional feature, but for now I'm deciding it's outside of the scope of this project.
