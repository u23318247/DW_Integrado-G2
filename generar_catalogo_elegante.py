import json

# Categorias formales y elegantes (9 categorias x 20 productos = 180 productos)

# URLs de alta resolución enfocadas en ROPA ELEGANTE, FORMAL, SASTRERIA, ALTA COSTURA
IMAGES_ELEGANTE = {
    4: [ # Calzado Formal & Mocasines (20)
        "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=900&auto=format&fit=crop&q=80", # Oxford shoes
        "https://images.unsplash.com/photo-1533867617858-e7b97e060509?w=900&auto=format&fit=crop&q=80", # Leather loafers
        "https://images.unsplash.com/photo-1543163521-1bf539c55dd2?w=900&auto=format&fit=crop&q=80", # High heels / stiletto
        "https://images.unsplash.com/photo-1582897165039-4d693f44c66e?w=900&auto=format&fit=crop&q=80", # Derby shoes
        "https://images.unsplash.com/photo-1560343090-f0409e92791a?w=900&auto=format&fit=crop&q=80", # Elegant dress shoes
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=900&auto=format&fit=crop&q=80", # Formal footwear
        "https://images.unsplash.com/photo-1535043934128-cf0b28d52f95?w=900&auto=format&fit=crop&q=80", # Classic leather
        "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=900&auto=format&fit=crop&q=80", # Formal shoe
        "https://images.unsplash.com/photo-1581044777550-4cfa60707c03?w=900&auto=format&fit=crop&q=80", # Classic black dress shoe
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=900&auto=format&fit=crop&q=80", # Designer shoe
        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=900&auto=format&fit=crop&q=80", # Brogues
        "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=900&auto=format&fit=crop&q=80", # Monkstrap
        "https://images.unsplash.com/photo-1512374382149-233c42b6a83b?w=900&auto=format&fit=crop&q=80", # Leather shoes
        "https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=900&auto=format&fit=crop&q=80", # Polished dress shoe
        "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=900&auto=format&fit=crop&q=80", # Stiletto high heels
        "https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=900&auto=format&fit=crop&q=80", # Loafers formal
        "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=900&auto=format&fit=crop&q=80", # Luxury oxford
        "https://images.unsplash.com/photo-1539185441755-769473a23570?w=900&auto=format&fit=crop&q=80", # Classic leather shoe
        "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=900&auto=format&fit=crop&q=80", # Elegant moccasin
        "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=900&auto=format&fit=crop&q=80"  # Formal black shoe
    ],
    1: [ # Trajes, Blazers & Camisas (20)
        "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=900&auto=format&fit=crop&q=80", # Men suit / blazer
        "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=900&auto=format&fit=crop&q=80", # Business suit tie
        "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?w=900&auto=format&fit=crop&q=80", # Elegant blazer
        "https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=900&auto=format&fit=crop&q=80", # White dress shirt
        "https://images.unsplash.com/photo-1592878904946-b3cd8ae243d0?w=900&auto=format&fit=crop&q=80", # Woman elegant suit
        "https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=900&auto=format&fit=crop&q=80", # Tailored suit
        "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=900&auto=format&fit=crop&q=80", # Fine shirt
        "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=900&auto=format&fit=crop&q=80", # Elegant coat / blazer
        "https://images.unsplash.com/photo-1552374196-1ab2a1c593e8?w=900&auto=format&fit=crop&q=80", # Formal suit
        "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=900&auto=format&fit=crop&q=80", # Dress shirt
        "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=900&auto=format&fit=crop&q=80", # Navy suit
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=900&auto=format&fit=crop&q=80", # Formal portrait suit
        "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=900&auto=format&fit=crop&q=80", # High fashion elegant
        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=900&auto=format&fit=crop&q=80", # Elegant menswear
        "https://images.unsplash.com/photo-1487222477894-8943e31ef7b2?w=900&auto=format&fit=crop&q=80", # Formal vest and suit
        "https://images.unsplash.com/photo-1485230895905-ec40ba36b9bc?w=900&auto=format&fit=crop&q=80", # Elegant blouse / blazer
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=900&auto=format&fit=crop&q=80", # Woman elegant dress blazer
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=900&auto=format&fit=crop&q=80", # Men formal shirt
        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=900&auto=format&fit=crop&q=80", # Executive formal
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=900&auto=format&fit=crop&q=80"  # Tailored jacket
    ],
    2: [ # Polos Pima & Blusas de Seda (20)
        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80", # Premium polo
        "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80", # Black polo pima
        "https://images.unsplash.com/photo-1562157873-818bc0726f68?w=900&auto=format&fit=crop&q=80", # Silk blouse
        "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=900&auto=format&fit=crop&q=80", # Elegant top
        "https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?w=900&auto=format&fit=crop&q=80", # White polo formal
        "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=900&auto=format&fit=crop&q=80", # Navy polo
        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=900&auto=format&fit=crop&q=80", # Pima cotton top
        "https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=900&auto=format&fit=crop&q=80", # Elegant button polo
        "https://images.unsplash.com/photo-1503342394128-c104d54dba01?w=900&auto=format&fit=crop&q=80", # Silk top
        "https://images.unsplash.com/photo-1622445268046-608b63e80cbb?w=900&auto=format&fit=crop&q=80", # Piqué luxury polo
        "https://images.unsplash.com/photo-1583744946564-b52ac1c389c8?w=900&auto=format&fit=crop&q=80", # Dark elegant polo
        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=900&auto=format&fit=crop&q=80", # Ivory blouse
        "https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?w=900&auto=format&fit=crop&q=80", # Satin top
        "https://images.unsplash.com/photo-1508427953056-b00b8d78ebf5?w=900&auto=format&fit=crop&q=80", # Clean polo
        "https://images.unsplash.com/photo-1527719327859-c6ce80353573?w=900&auto=format&fit=crop&q=80", # Classic white top
        "https://images.unsplash.com/photo-1571455786673-9d9d6c194f90?w=900&auto=format&fit=crop&q=80", # Pima mercerizado
        "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=900&auto=format&fit=crop&q=80", # Fine knit polo
        "https://images.unsplash.com/photo-1588731234159-8b9963143fca?w=900&auto=format&fit=crop&q=80", # Chiffon blouse
        "https://images.unsplash.com/photo-1503342452485-86b7f54527ef?w=900&auto=format&fit=crop&q=80", # Formal collar polo
        "https://images.unsplash.com/photo-1554412933-514a83d2f3c8?w=900&auto=format&fit=crop&q=80"  # Satin blouse
    ],
    6: [ # Abrigos de Paño & Gabardinas (20)
        "https://images.unsplash.com/photo-1544441893-675973e31985?w=900&auto=format&fit=crop&q=80", # Wool overcoat
        "https://images.unsplash.com/photo-1520975954732-35dd22299614?w=900&auto=format&fit=crop&q=80", # Trench coat
        "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=900&auto=format&fit=crop&q=80", # Elegant cashmere coat
        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=900&auto=format&fit=crop&q=80", # Long wool overcoat
        "https://images.unsplash.com/photo-1495105787522-5334e3ffa0ef?w=900&auto=format&fit=crop&q=80", # Formal jacket
        "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=900&auto=format&fit=crop&q=80", # Double breasted coat
        "https://images.unsplash.com/photo-1548883354-93339173ebef?w=900&auto=format&fit=crop&q=80", # Camel trench coat
        "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=900&auto=format&fit=crop&q=80", # High fashion coat
        "https://images.unsplash.com/photo-1559551409-dadc959f76b8?w=900&auto=format&fit=crop&q=80", # Executive overcoat
        "https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?w=900&auto=format&fit=crop&q=80", # Peacoat
        "https://images.unsplash.com/photo-1543087903-1ac2ec7aa8c5?w=900&auto=format&fit=crop&q=80", # Alpaca wool coat
        "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?w=900&auto=format&fit=crop&q=80", # Wool cape coat
        "https://images.unsplash.com/photo-1557453434-62e92c2b7405?w=900&auto=format&fit=crop&q=80", # Navy overcoat
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=900&auto=format&fit=crop&q=80", # Black wool coat
        "https://images.unsplash.com/photo-1509551388413-e18d0ac5d495?w=900&auto=format&fit=crop&q=80", # Wool long coat
        "https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=900&auto=format&fit=crop&q=80", # Sarto coat
        "https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=900&auto=format&fit=crop&q=80", # Dark trench
        "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=900&auto=format&fit=crop&q=80", # Structured coat
        "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=900&auto=format&fit=crop&q=80", # Tailored coat
        "https://images.unsplash.com/photo-1544441893-675973e31985?w=900&auto=format&fit=crop&q=80"  # Cashmere wrap
    ],
    3: [ # Pantalones de Vestir & Sastreros (20)
        "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=900&auto=format&fit=crop&q=80", # Formal trousers
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=900&auto=format&fit=crop&q=80", # Tailored pants
        "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=900&auto=format&fit=crop&q=80", # Chino formal
        "https://images.unsplash.com/photo-1584370848010-d7fe6bc767ec?w=900&auto=format&fit=crop&q=80", # Crepe palazzo
        "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=900&auto=format&fit=crop&q=80", # Dress pants
        "https://images.unsplash.com/photo-1517445312882-bc9910d016b7?w=900&auto=format&fit=crop&q=80", # Sastrero
        "https://images.unsplash.com/photo-1582552938357-32b906dfca00?w=900&auto=format&fit=crop&q=80", # Pleated trousers
        "https://images.unsplash.com/photo-1511196041699-28c0b9a38f38?w=900&auto=format&fit=crop&q=80", # Wool trousers
        "https://images.unsplash.com/photo-1604176354204-9268737828e4?w=900&auto=format&fit=crop&q=80", # High waist trousers
        "https://images.unsplash.com/photo-1582418702059-97ebafb35d09?w=900&auto=format&fit=crop&q=80", # Tailored slim fit
        "https://images.unsplash.com/photo-1560243563-062bfc001d68?w=900&auto=format&fit=crop&q=80", # Elegant pants
        "https://images.unsplash.com/photo-1551854838-212c50b4c184?w=900&auto=format&fit=crop&q=80", # Navy dress trousers
        "https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=900&auto=format&fit=crop&q=80", # Classic charcoal pants
        "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80", # Sastrero pinzas
        "https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=900&auto=format&fit=crop&q=80", # Black dress pants
        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80", # Beige tailored pants
        "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=900&auto=format&fit=crop&q=80", # White formal pants
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=900&auto=format&fit=crop&q=80", # Sastrero fino
        "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=900&auto=format&fit=crop&q=80", # Italian fit pants
        "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=900&auto=format&fit=crop&q=80"  # Sastrero lino
    ],
    7: [ # Sombreros Clásicos & Boinas (20)
        "https://images.unsplash.com/photo-1513373090673-c160c23a3d67?w=900&auto=format&fit=crop&q=80", # Fedora hat
        "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=900&auto=format&fit=crop&q=80", # Classic fedora
        "https://images.unsplash.com/photo-1575428652377-a2d80e2277fc?w=900&auto=format&fit=crop&q=80", # Panama hat
        "https://images.unsplash.com/photo-1534215754734-18e55d13e346?w=900&auto=format&fit=crop&q=80", # French beret
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=900&auto=format&fit=crop&q=80", # Wool beret
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=900&auto=format&fit=crop&q=80", # Tweed flat cap
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=900&auto=format&fit=crop&q=80", # Gentleman hat
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=900&auto=format&fit=crop&q=80", # Trilby hat
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=900&auto=format&fit=crop&q=80", # Panama straw
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=900&auto=format&fit=crop&q=80", # Elegant cloche
        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=900&auto=format&fit=crop&q=80", # Black fedora
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=900&auto=format&fit=crop&q=80", # Felt hat
        "https://images.unsplash.com/photo-1513373090673-c160c23a3d67?w=900&auto=format&fit=crop&q=80", # Camel fedora
        "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=900&auto=format&fit=crop&q=80", # Newsboy cap
        "https://images.unsplash.com/photo-1575428652377-a2d80e2277fc?w=900&auto=format&fit=crop&q=80", # Straw boater
        "https://images.unsplash.com/photo-1534215754734-18e55d13e346?w=900&auto=format&fit=crop&q=80", # Wool felt beret
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=900&auto=format&fit=crop&q=80", # Vintage bowler hat
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=900&auto=format&fit=crop&q=80", # Fedora ribbon
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=900&auto=format&fit=crop&q=80", # Classic beret
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=900&auto=format&fit=crop&q=80"  # Gentleman wool cap
    ],
    5: [ # Portafolios & Accesorios de Cuero (20)
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=900&auto=format&fit=crop&q=80", # Leather briefcase
        "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=900&auto=format&fit=crop&q=80", # Luxury leather bag
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=900&auto=format&fit=crop&q=80", # Executive handbag
        "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=900&auto=format&fit=crop&q=80", # Leather belt
        "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=900&auto=format&fit=crop&q=80", # Silk tie
        "https://images.unsplash.com/photo-1544816155-12df9643f363?w=900&auto=format&fit=crop&q=80", # Leather portfolio
        "https://images.unsplash.com/photo-1600857062241-98e5dba7f214?w=900&auto=format&fit=crop&q=80", # Pocket square tie
        "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?w=900&auto=format&fit=crop&q=80", # Leather wallet
        "https://images.unsplash.com/photo-1509741102003-ca64bfe5f069?w=900&auto=format&fit=crop&q=80", # Executive bag
        "https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=900&auto=format&fit=crop&q=80", # Briefcase
        "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=900&auto=format&fit=crop&q=80", # Leather accessories
        "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=900&auto=format&fit=crop&q=80", # Luxury belt
        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=900&auto=format&fit=crop&q=80", # Silk tie set
        "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=900&auto=format&fit=crop&q=80", # Fine leather
        "https://images.unsplash.com/photo-1508296695146-257a814070b4?w=900&auto=format&fit=crop&q=80", # Leather satchel
        "https://images.unsplash.com/photo-1563903530908-afdd155d057a?w=900&auto=format&fit=crop&q=80", # Executive travel bag
        "https://images.unsplash.com/photo-1614179689702-355944cf0918?w=900&auto=format&fit=crop&q=80", # Suspenders / tirantes
        "https://images.unsplash.com/photo-1598532163257-ae3c6b2524b6?w=900&auto=format&fit=crop&q=80", # Formal wallet
        "https://images.unsplash.com/photo-1544816155-12df9643f363?w=900&auto=format&fit=crop&q=80", # Leather document holder
        "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=900&auto=format&fit=crop&q=80"  # Executive leather case
    ],
    8: [ # Calcetines Ejecutivos & Medias Finas (20)
        "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80", # Executive socks
        "https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?w=900&auto=format&fit=crop&q=80", # Fine cotton socks
        "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=900&auto=format&fit=crop&q=80", # Mercerized socks
        "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=900&auto=format&fit=crop&q=80", # Dress socks
        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=900&auto=format&fit=crop&q=80", # Formal socks
        "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=900&auto=format&fit=crop&q=80", # Dark socks
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=900&auto=format&fit=crop&q=80", # Business socks
        "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80", # Luxury socks
        "https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?w=900&auto=format&fit=crop&q=80", # Wool socks
        "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=900&auto=format&fit=crop&q=80", # Formal wear socks
        "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=900&auto=format&fit=crop&q=80", # Black dress socks
        "https://images.unsplash.com/photo-1512374382149-233c42b6a83b?w=900&auto=format&fit=crop&q=80", # Silk socks
        "https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=900&auto=format&fit=crop&q=80", # Thin pantyhose
        "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=900&auto=format&fit=crop&q=80", # Elegant hosiery
        "https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=900&auto=format&fit=crop&q=80", # Executive socks
        "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=900&auto=format&fit=crop&q=80", # Ribbed socks
        "https://images.unsplash.com/photo-1539185441755-769473a23570?w=900&auto=format&fit=crop&q=80", # Navy dress socks
        "https://images.unsplash.com/photo-1588117260148-b47818741c74?w=900&auto=format&fit=crop&q=80", # Charcoal socks
        "https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?w=900&auto=format&fit=crop&q=80", # Fine knit
        "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=900&auto=format&fit=crop&q=80"  # Escocia socks
    ],
    9: [ # Relojería & Joyería Fina (20)
        "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=900&auto=format&fit=crop&q=80", # Luxury watch
        "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80", # Fine cufflinks / jewelry
        "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=900&auto=format&fit=crop&q=80", # Pearl earrings
        "https://images.unsplash.com/photo-1611591475152-477d8c53e707?w=900&auto=format&fit=crop&q=80", # Gold necklace
        "https://images.unsplash.com/photo-1602751584552-8ba73aad10e1?w=900&auto=format&fit=crop&q=80", # Silver cufflinks
        "https://images.unsplash.com/photo-1598560917505-59a3ad559071?w=900&auto=format&fit=crop&q=80", # Elegant watch
        "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=900&auto=format&fit=crop&q=80", # Solitaire ring
        "https://images.unsplash.com/photo-1573408301185-9146fe634ad0?w=900&auto=format&fit=crop&q=80", # Lapel pin
        "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=900&auto=format&fit=crop&q=80", # Gold bracelet
        "https://images.unsplash.com/photo-1600003014755-ba31aa59c4b6?w=900&auto=format&fit=crop&q=80", # Tie clip
        "https://images.unsplash.com/photo-1617038220319-276d3cfab638?w=900&auto=format&fit=crop&q=80", # Pearl necklace
        "https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=900&auto=format&fit=crop&q=80", # Automatic watch
        "https://images.unsplash.com/photo-1599643477877-530eb83abc8e?w=900&auto=format&fit=crop&q=80", # Luxury brooch
        "https://images.unsplash.com/photo-1603561591411-07134e71a2a9?w=900&auto=format&fit=crop&q=80", # Gold cufflinks
        "https://images.unsplash.com/photo-1588444837495-c6cfeb53f32d?w=900&auto=format&fit=crop&q=80", # Diamond earrings
        "https://images.unsplash.com/photo-1590548784585-645d89059db8?w=900&auto=format&fit=crop&q=80", # Watch dial
        "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=900&auto=format&fit=crop&q=80", # Pearl studs
        "https://images.unsplash.com/photo-1611591475152-477d8c53e707?w=900&auto=format&fit=crop&q=80", # Silver bracelet
        "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80", # Onyx cufflinks
        "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=900&auto=format&fit=crop&q=80"  # Luxury lapel pin
    ]
}

# 20 Elegant & Formal Products per category block
CAT_PRODUCTS_ELEGANTE = {
    4: [ # Calzado (Zapatos Formales & Mocasines)
        ("Zapato Oxford Clásico Charol", "Hombre", 289.90),
        ("Mocasín Penny Loafer Cuero Coñac", "Hombre", 259.90),
        ("Zapato Derby Sastrero Café", "Hombre", 249.90),
        ("Zapato Monkstrap Doble Hebilla", "Hombre", 279.90),
        ("Stiletto Tacón Alto Charol Negro", "Mujer", 229.90),
        ("Mocasín Tassel con Borlas Cuero", "Hombre", 269.90),
        ("Zapato Oxford Brogue Semi-Perforado", "Hombre", 275.00),
        ("Tacón Slingback Elegante Nude", "Mujer", 219.90),
        ("Zapato Derby Cap Toe Ejecutivo", "Hombre", 265.00),
        ("Mocasín Terciopelo Smoking Gala", "Hombre", 299.90),
        ("Zapato Brogue Wingtip Artesanal", "Hombre", 285.00),
        ("Botín Chelsea Cuero Formal", "Unisex", 295.00),
        ("Tacón Clásico Piel de Ternera", "Mujer", 239.00),
        ("Zapato Oxford Gala Charol Negro", "Hombre", 319.90),
        ("Mocasín Driving Shoe Cuero Grano", "Hombre", 245.00),
        ("Zapato Derby Sastrero Borgoña", "Hombre", 269.00),
        ("Stiletto Elegante Terciopelo Azul", "Mujer", 225.00),
        ("Zapato Monkstrap Hebilla Dorada", "Hombre", 289.00),
        ("Mocasín Bit Loafer Detalle Metálico", "Unisex", 279.00),
        ("Zapato Oxford Wholecut Una Pieza", "Hombre", 349.90),
    ],
    1: [ # Superiores (Trajes, Blazers & Camisas)
        ("Blazer Sastrero Dos Botones Marino", "Hombre", 349.90),
        ("Camisa de Vestir Pima Blanco Puro", "Hombre", 149.90),
        ("Traje Ejecutivo Slim Fit Antracita", "Hombre", 489.90),
        ("Blazer Cruzado Double-Breasted Negro", "Hombre", 389.90),
        ("Camisa Cuello Francés Celeste Marfil", "Hombre", 159.90),
        ("Traje Smoking Gala Solapa Raso", "Hombre", 549.90),
        ("Camisa Cuello Italiano Raya Diplomática", "Hombre", 165.00),
        ("Blazer de Terciopelo Burdeos", "Unisex", 379.90),
        ("Chaleco Sastrero Espiga Inglés", "Hombre", 189.90),
        ("Camisa Lino Fino Cuello Mao", "Hombre", 145.00),
        ("Blazer Lino Italiano Beige Arena", "Unisex", 329.90),
        ("Traje Tres Piezas Príncipe de Gales", "Hombre", 599.90),
        ("Camisa de Vestir Puño Francés Doble", "Hombre", 175.00),
        ("Blazer Entallado Jacquard Nocturno", "Mujer", 369.00),
        ("Camisa Smoking Pechera Plisada", "Hombre", 185.00),
        ("Blazer de Paño Fino Cuadros Ventana", "Hombre", 359.00),
        ("Camisa Algodón Egipcio Blanco Puro", "Mujer", 155.00),
        ("Traje Sastrero Lana Fría Gris Perla", "Hombre", 519.90),
        ("Chaleco de Vestir Solapa Chal", "Hombre", 195.00),
        ("Blazer Sastrero Marfil de Banquete", "Mujer", 385.00),
    ],
    2: [ # Polos (Polos Pima & Blusas de Seda)
        ("Polo Camisero Algodón Pima 100/2 Negro", "Hombre", 119.90),
        ("Blusa de Satén Manga Larga Perla", "Mujer", 139.90),
        ("Polo Piqué de Lujo Cuello Camisero", "Hombre", 109.90),
        ("Blusa de Seda con Lazo Cuello Marfil", "Mujer", 159.90),
        ("Polo Camisero Mercerizado Marino", "Hombre", 125.00),
        ("Blusa Elegante Escote V Champagne", "Mujer", 129.90),
        ("Polo Punto Fino Seda-Algodón", "Unisex", 135.00),
        ("Blusa Chifón Mangas Abullonadas", "Mujer", 145.00),
        ("Polo Camisero Minimal Blanco Ópalo", "Hombre", 115.00),
        ("Blusa Cruzada Wrap Satén Esmeralda", "Mujer", 149.00),
        ("Polo Piqué Premium Gris Oxford", "Hombre", 112.00),
        ("Blusa Formal con Pliegues Sutiles", "Mujer", 138.00),
        ("Polo Camisero Tejido Fino Borgoña", "Hombre", 122.00),
        ("Blusa Encaje Fino Gala Noche", "Mujer", 169.00),
        ("Polo Pima Cuello Camp Collar Lujo", "Hombre", 129.00),
        ("Blusa de Seda Cuello Mao Dorada", "Mujer", 155.00),
        ("Polo Camisero Verde Inglés Formal", "Hombre", 118.00),
        ("Blusa Georgette Formal Nude Pastel", "Mujer", 132.00),
        ("Polo Pima Acanalado Fino Ejecutivo", "Hombre", 126.00),
        ("Blusa Formal Asimétrica Crepé Negro", "Mujer", 142.00),
    ],
    6: [ # Abrigos (Abrigos de Paño & Gabardinas)
        ("Abrigo Largo de Paño y Lana Negro", "Unisex", 429.90),
        ("Gabardina Trench Coat Clásica Beige", "Unisex", 389.90),
        ("Sobretodo de Lana Cashmere Camel", "Hombre", 469.90),
        ("Abrigo Chesterfield Azul Noche", "Hombre", 449.90),
        ("Tapado Ejecutivo Lana Merino Gris", "Mujer", 399.90),
        ("Capa de Paño Fino Botones Dorados", "Mujer", 359.90),
        ("Abrigo de Paño Espiga Corte Italiano", "Hombre", 439.00),
        ("Gabardina Entallada Impermeable Negra", "Mujer", 379.00),
        ("Abrigo Largo Solapa Muesca Borgoña", "Unisex", 419.00),
        ("Sobretodo Doble Faz Lana Pura", "Hombre", 489.00),
        ("Abrigo Lana de Alpaca Premium Perú", "Unisex", 499.90),
        ("Gabardina Doble Botonadura Tradicional", "Hombre", 395.00),
        ("Abrigo Minimal Sin Costuras Camel", "Mujer", 385.00),
        ("Chaquetón Marinero Peacoat Lana", "Hombre", 369.00),
        ("Abrigo Gala Cuello Chal Terciopelo", "Mujer", 445.00),
        ("Tapado Sastrero Estructurado Arena", "Mujer", 375.00),
        ("Abrigo Largo Paño Verde Botella", "Hombre", 425.00),
        ("Gabardina Corta Ejecutiva Dark Navy", "Hombre", 349.00),
        ("Sobretodo Clásico Solapa de Pico", "Hombre", 459.00),
        ("Abrigo Invierno Lana Virgen Marfil", "Mujer", 439.00),
    ],
    3: [ # Pantalones (Pantalones de Vestir & Sastreros)
        ("Pantalón de Vestir Sastrero Negro", "Hombre", 179.90),
        ("Pantalón con Pinzas Tiro Alto Marino", "Hombre", 189.90),
        ("Pantalón Palazzo Fluido Crepé Marfil", "Mujer", 169.90),
        ("Pantalón Chino Formal Lino Beige", "Hombre", 159.90),
        ("Pantalón Sastrero Slim Fit Gris Oxford", "Hombre", 175.00),
        ("Pantalón de Vestir Tiro Medio Pliegues", "Mujer", 165.00),
        ("Pantalón Culotte Elegante Perla", "Mujer", 155.00),
        ("Pantalón Smoking Banda Lateral Raso", "Hombre", 219.90),
        ("Pantalón Sastrero Lana Fría Carbón", "Hombre", 195.00),
        ("Pantalón Recto Vestir Terracota Fino", "Mujer", 168.00),
        ("Pantalón Palazzo Cinturón Forrado", "Mujer", 179.00),
        ("Pantalón Pinzas Doble Pliegue Camel", "Hombre", 185.00),
        ("Pantalón Sastrero Micro-Houndstooth", "Hombre", 189.00),
        ("Pantalón Formal Pernera Ancha Nude", "Mujer", 159.00),
        ("Pantalón Vestir Confort Elástico", "Hombre", 169.00),
        ("Pantalón Pinzas Azul Prusia Sastrería", "Hombre", 182.00),
        ("Pantalón Sastrero Blanco Marfil", "Mujer", 174.00),
        ("Pantalón Formal Tejido Sarga Gris", "Hombre", 168.00),
        ("Pantalón Vestir Dobladillo Italiano", "Hombre", 179.90),
        ("Pantalón Sastrero Corte Recto Borgoña", "Unisex", 185.00),
    ],
    7: [ # Sombreros Clásicos & Boinas
        ("Sombrero Fedora Fieltro Lana Negro", "Unisex", 129.90),
        ("Boina Francesa Lana Clásica Negra", "Mujer", 89.90),
        ("Sombrero Panamá Genuino Paja Toquilla", "Unisex", 189.90),
        ("Boina Inglesa Flat Cap Tweed Espiga", "Hombre", 99.90),
        ("Sombrero Fedora Ala Ancha Camel", "Mujer", 139.90),
        ("Sombrero Porkpie Clásico Lana Fina", "Hombre", 119.90),
        ("Boina de Lana Merino con Visera", "Hombre", 95.00),
        ("Sombrero Canotier Cinta Raso Negra", "Unisex", 110.00),
        ("Sombrero Trilby Elegante Gris Carbón", "Hombre", 125.00),
        ("Boina Francesa Bordada Minimal", "Mujer", 95.00),
        ("Sombrero Fedora Cinta Seda Marino", "Hombre", 135.00),
        ("Boina Gatsby Estilo Newsboy Tweed", "Hombre", 105.00),
        ("Sombrero Campana Cloche Años 20", "Mujer", 115.00),
        ("Sombrero Fedora Fieltro Blanco Marfil", "Mujer", 145.00),
        ("Boina Clásica Paño Fino Vino Tinto", "Mujer", 89.00),
        ("Sombrero Panamá Cinta Raso Lujo", "Hombre", 199.90),
        ("Sombrero Bombín Clásico Británico", "Hombre", 149.00),
        ("Boina Escocesa Cuadros Tartán Fino", "Unisex", 92.00),
        ("Sombrero Safari Lino y Algodón Fino", "Unisex", 118.00),
        ("Sombrero Fedora Fieltro Verde Botella", "Unisex", 139.00),
    ],
    5: [ # Portafolios & Accesorios de Cuero
        ("Portafolio Ejecutivo Cuero Negro", "Unisex", 299.90),
        ("Corbata de Seda Pura Jacquard Marino", "Hombre", 89.90),
        ("Cinturón Reversible Cuero Italiano", "Hombre", 119.90),
        ("Maletín de Mano para Documentos Café", "Unisex", 279.90),
        ("Set de Corbatín Pajarita y Pañuelo", "Hombre", 99.90),
        ("Billetera Bifold Cuero Grano Fino", "Hombre", 95.00),
        ("Tirantes Sastreros Botón Elásticos", "Hombre", 85.00),
        ("Pañuelo de Bolsillo Seda Estampada", "Hombre", 59.90),
        ("Tarjetero Metálico Forro de Cuero", "Unisex", 69.90),
        ("Neceser Ejecutivo Cuero Nobuck", "Unisex", 129.90),
        ("Maletín Porta Laptop Sastrero Cuero", "Unisex", 319.90),
        ("Corbata de Seda Paisley Fiesta", "Hombre", 95.00),
        ("Cinturón de Vestir Hebilla Extraplana", "Hombre", 109.00),
        ("Pasador de Corbata Plata Esterlina", "Hombre", 75.00),
        ("Portadocumentos Pasaporte Piel Fina", "Unisex", 89.00),
        ("Tirantes Cruzados de Seda Negro Gala", "Hombre", 98.00),
        ("Pañuelo de Solapa Lino Blanco Puro", "Hombre", 49.90),
        ("Neceser de Cuero Vintage Borgoña", "Unisex", 135.00),
        ("Corbata de Punto Seda Formal Gris", "Hombre", 89.00),
        ("Maletín Ejecutivo Rígido con Cerradura", "Hombre", 359.90),
    ],
    8: [ # Calcetines Ejecutivos & Medias Finas
        ("Calcetín Ejecutivo Hilo de Escocia Negro", "Hombre", 39.90),
        ("Calcetín de Vestir Canalé Azul Marino", "Hombre", 35.00),
        ("Pantis Finas 15 Denier Transparentes", "Mujer", 42.00),
        ("Calcetín Mercerizado Gris Oxford", "Hombre", 38.00),
        ("Calcetín Algodón Pima Burdeos Lujo", "Hombre", 39.00),
        ("Pantis Satinadas Antideslizantes Nude", "Mujer", 45.00),
        ("Calcetín de Vestir Micro-Puntos Plata", "Hombre", 36.00),
        ("Calcetín Largo Hasta Rodilla Marino", "Hombre", 44.00),
        ("Calcetín Lana Fina Merino Ejecutivo", "Hombre", 49.90),
        ("Pantis Efecto Moldeador Translúcidas", "Mujer", 48.00),
        ("Calcetín Algodón Peinado Canalé Marrón", "Hombre", 37.00),
        ("Calcetín Ejecutivo Sin Elástico Opresor", "Hombre", 39.00),
        ("Calcetín de Vestir Raya Diplomática", "Hombre", 38.50),
        ("Pantis Opacas Elegantes 40 Denier", "Mujer", 46.00),
        ("Calcetín de Vestir Doble Hilo Carbón", "Hombre", 39.90),
        ("Calcetín Mercerizado Verde Inglés", "Hombre", 38.00),
        ("Pack x3 Calcetines Ejecutivos Lujo", "Hombre", 89.90),
        ("Pantis Malla Fina Geometría Clásica", "Mujer", 44.00),
        ("Calcetín Largo Bambú Transpirable", "Hombre", 42.00),
        ("Calcetín Sastrero Fino Azul Noche", "Hombre", 39.00),
    ],
    9: [ # Relojería & Joyería Fina
        ("Reloj de Vestir Correa de Cuero Suizo", "Hombre", 429.90),
        ("Mancuernillas Gemelos Plata de Ley 925", "Hombre", 149.90),
        ("Reloj Automático Dial Negro Acero", "Hombre", 479.90),
        ("Cadena Fina Oro 18k Minimalista", "Mujer", 289.90),
        ("Collar de Perlas Cultivadas Agua Dulce", "Mujer", 329.90),
        ("Gemelos de Solapa con Ónix Negro", "Hombre", 169.90),
        ("Reloj Cronógrafo Elegante Cristal Zafiro", "Hombre", 499.90),
        ("Aretes de Perla Clásica Broche Oro", "Mujer", 199.90),
        ("Pisacorbata Plata con Grabado Sutil", "Hombre", 119.90),
        ("Pulsera Rígida Brazalete Plata Fina", "Mujer", 219.90),
        ("Reloj Caja Extraplana Malla Milanesa", "Unisex", 389.90),
        ("Gemelos Madreperla Boda y Gala", "Hombre", 159.90),
        ("Pin Metálico Solapa Flor Sastrera", "Hombre", 79.90),
        ("Anillo Solitario Circonia Corte Brillante", "Mujer", 189.90),
        ("Reloj Clásico Esfera Blanca Oro Rosa", "Mujer", 419.90),
        ("Collar Dije Barra Vertical Oro Blanco", "Mujer", 249.90),
        ("Pulsera Eslabón Fino Cuero y Plata", "Hombre", 139.90),
        ("Gemelos Acero Quirúrgico Forma Nudo", "Hombre", 129.90),
        ("Aretes Largos Lineales Baño de Oro", "Mujer", 149.00),
        ("Pin de Solapa Emblema Corona Real", "Hombre", 85.00),
    ]
}

# Real formal & elegant color palette (AMPLIADA CON MÁS COLORES)
# 6 a 7 colores por prenda
COLORES_ROPA = [
    "Negro Azabache",
    "Azul Marino",
    "Blanco Marfil",
    "Gris Oxford",
    "Vino Tinto",
    "Beige Camel",
    "Verde Esmeralda"
]

COLORES_CALZADO = [
    "Negro Azabache",
    "Café Coñac",
    "Azul Marino",
    "Beige Camel",
    "Vino Tinto",
    "Blanco Marfil"
]

COLORES_ACCESORIOS = [
    "Negro Azabache",
    "Café Coñac",
    "Azul Marino",
    "Beige Camel",
    "Vino Tinto",
    "Azul Noche"
]

COLORES_SOMBREROS = [
    "Negro Azabache",
    "Beige Camel",
    "Gris Oxford",
    "Azul Marino",
    "Blanco Marfil",
    "Vino Tinto"
]

COLORES_JOYERIA = [
    "Plateado",
    "Dorado",
    "Oro Rosa",
    "Negro Azabache",
    "Azul Noche",
    "Blanco Perla"
]

TALLAS_ELEGANTE = {
    4: ["40", "41", "42"],
    1: ["38R", "40R", "42R"],
    2: ["S", "M", "L"],
    6: ["S", "M", "L"],
    3: ["30", "32", "34"],
    7: ["Única", "Ajustable"],
    5: ["Única"],
    8: ["39-42", "43-45"],
    9: ["Única", "Ajustable"]
}

sql_lines = []
sql_lines.append("-- LIMPIEZA Y REGENERACION COMPLETA CON MAS COLORES (6 A 7 POR PRENDA)")
sql_lines.append("TRUNCATE TABLE producto_detalles, productos RESTART IDENTITY CASCADE;")
sql_lines.append("")

prod_id = 1
detalle_id = 1

for cat_id in sorted(CAT_PRODUCTS_ELEGANTE.keys()):
    prods = CAT_PRODUCTS_ELEGANTE[cat_id]
    images = IMAGES_ELEGANTE[cat_id]
    tallas = TALLAS_ELEGANTE[cat_id]
    
    # Asignar paleta de colores ampliada según categoría
    if cat_id == 4:
        palette = COLORES_CALZADO
    elif cat_id == 9:
        palette = COLORES_JOYERIA
    elif cat_id == 5:
        palette = COLORES_ACCESORIOS
    elif cat_id == 7:
        palette = COLORES_SOMBREROS
    else:
        palette = COLORES_ROPA
    
    for i, (nombre, genero, precio) in enumerate(prods):
        img = images[i % len(images)]
        sql_lines.append(f"INSERT INTO productos (id, categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES ({prod_id}, {cat_id}, '{nombre}', '{genero}', '{img}', {precio:.2f}, true);")
        
        # Generar variantes con todos los colores de la paleta
        for c_idx, color in enumerate(palette):
            for t_idx, talla in enumerate(tallas[:2]): # 2 tallas x 6 colores = 12 variantes por producto
                codigo = f"ELG{prod_id:03d}-C{c_idx+1}T{t_idx+1}"
                nombre_comp = f"{nombre} {color} {talla}"
                desc = f"Prenda sastrera LMLL en color {color}. Tejidos nobles de alta gama."
                stock = 15 + ((prod_id * 3 + c_idx * 7 + t_idx * 11) % 35)
                
                sql_lines.append(f"INSERT INTO producto_detalles (id, producto_id, nombre_completo, codigo, imagen_url, marca, talla, color, descripcion, stock, precio_base, precio_adicional) VALUES ({detalle_id}, {prod_id}, '{nombre_comp}', '{codigo}', '{img}', 'LMLL Haute Couture', '{talla}', '{color}', '{desc}', {stock}, {precio:.2f}, 0.00);")
                detalle_id += 1
                
        prod_id += 1

sql_lines.append("")
sql_lines.append(f"SELECT setval('productos_id_seq', {prod_id - 1});")
sql_lines.append(f"SELECT setval('producto_detalles_id_seq', {detalle_id - 1});")

with open("seed_elegante_20_por_bloque.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql_lines))

print(f"Catálogo Elegante generado con {prod_id - 1} productos y {detalle_id - 1} variantes (más colores añadidos).")
