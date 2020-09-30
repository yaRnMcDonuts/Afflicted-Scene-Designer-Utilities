# ASD-Utilities

Utility Library for games that use features from the Afflicted Scene Designer. 

Contains:

TerrainShaderConverter - Converts terrains from Phong Terrain > PBR Terrain > Advanced PBR Terrains, and can revert as well. 

CatalogManager - register Models, Decals, and Terrain Textures into Asset Catalogs in order to store extra data when using the AfflictedSceneDesigner. This library is necissary in your game if you want to use the TerrainShaderConverter with AdvancedPbrTerrains.

TextureChannelPacker- Packs multiple grayscale textures into a metallicRoughnessAmbientOcclusion map, or a normal map and prallax into a packedNormalParallaxMap. Can also be used to pack any texture channels in any other way you may desire.

TextureArrayManager - Used internally by the TerrainShaderConverter to manage texture arrays when using the Advanced PBR Terrain shader. 
