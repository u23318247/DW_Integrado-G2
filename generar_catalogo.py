import json
import random

# Categorias:
# 1: Superiores
# 2: Polos
# 3: Pantalones
# 4: Calzado
# 5: Accesorios
# 6: Abrigos
# 7: Gorras y Chullos
# 8: Medias
# 9: Joyería y Pines

# Fashion images collections with verified Unsplash / Pexels URLs
IMAGES = {
    4: [ # Calzado (20)
        "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=900",
        "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?auto=compress&cs=tinysrgb&w=900",
        "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1512374382149-233c42b6a83b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588117260148-b47818741c74?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539185441755-769473a23570?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=900&auto=format&fit=crop&q=80"
    ],
    2: [ # Polos (20)
        "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1562157873-818bc0726f68?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1586790170083-2f9ceadc732d?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503342394128-c104d54dba01?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1622445268046-608b63e80cbb?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1583744946564-b52ac1c389c8?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503341455253-b2e723bb3dbb?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1508427953056-b00b8d78ebf5?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1527719327859-c6ce80353573?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1571455786673-9d9d6c194f90?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588731234159-8b9963143fca?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503342452485-86b7f54527ef?w=900&auto=format&fit=crop&q=80"
    ],
    6: [ # Abrigos y Casacas (20)
        "https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1544441893-675973e31985?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1551028719-00167b16eac5?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1520975954732-35dd22299614?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1495105787522-5334e3ffa0ef?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1548883354-93339173ebef?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1559551409-dadc959f76b8?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1543087903-1ac2ec7aa8c5?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1557453434-62e92c2b7405?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1509551388413-e18d0ac5d495?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=900&auto=format&fit=crop&q=80"
    ],
    3: [ # Pantalones (20)
        "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=900&auto=format&fit=crop&q=80",
        "https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=900",
        "https://images.unsplash.com/photo-1584370848010-d7fe6bc767ec?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1565084888279-aca607ecce0c?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1551854838-212c50b4c184?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517445312882-bc9910d016b7?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1582552938357-32b906dfca00?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511196041699-28c0b9a38f38?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1594633312681-425c7b97ccd1?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1604176354204-9268737828e4?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1582418702059-97ebafb35d09?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1560243563-062bfc001d68?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=900&auto=format&fit=crop&q=80"
    ],
    7: [ # Gorras y Chullos (20)
        "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534215754734-18e55d13e346?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1575428652377-a2d80e2277fc?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534215754734-18e55d13e346?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1513373090673-c160c23a3d67?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1529374255404-311a2a4f1fd9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=900&auto=format&fit=crop&q=80"
    ],
    1: [ # Superiores & Chompas (20)
        "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1543087903-1ac2ec7aa8c5?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1509551388413-e18d0ac5d495?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1485230895905-ec40ba36b9bc?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1516257984-b1b4d707412e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1554412933-514a83d2f3c8?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1529139574466-a303027c1d8b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1544441893-675973e31985?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80"
    ],
    5: [ # Accesorios & Mochilas (20)
        "https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg?auto=compress&cs=tinysrgb&w=900",
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1544816155-12df9643f363?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1575428652377-a2d80e2277fc?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1600857062241-98e5dba7f214?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511556532299-8f662fc26c06?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1509741102003-ca64bfe5f069?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1508296695146-257a814070b4?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1563903530908-afdd155d057a?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1614179689702-355944cf0918?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1598532163257-ae3c6b2524b6?w=900&auto=format&fit=crop&q=80"
    ],
    8: [ # Medias Urbanas (20)
        "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1607522370275-f14206abe5d3?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1586350977771-b3b0abd50c82?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1600185365926-3a2ce3cdb9eb?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1512374382149-233c42b6a83b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1515955656352-a1fa3ffcd111?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1551107696-a4b0c5a0d9a2?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1597045566677-8cf032ed6634?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1539185441755-769473a23570?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588117260148-b47818741c74?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1575537302964-96cd47c06b1b?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=900&auto=format&fit=crop&q=80"
    ],
    9: [ # Joyería & Pines (20)
        "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1611591475152-477d8c53e707?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1602751584552-8ba73aad10e1?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1598560917505-59a3ad559071?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1573408301185-9146fe634ad0?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1600003014755-ba31aa59c4b6?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1617038220319-276d3cfab638?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1599643477877-530eb83abc8e?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1603561591411-07134e71a2a9?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1588444837495-c6cfeb53f32d?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1590548784585-645d89059db8?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1611591475152-477d8c53e707?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=900&auto=format&fit=crop&q=80"
    ]
}

# Names for each category (20 each)
CAT_PRODUCTS = {
    4: [ # Calzado (4)
        ("Zapatilla Urbana", "Unisex", 149.90),
        ("Zapatilla Running", "Unisex", 169.90),
        ("Sneaker Retro White", "Unisex", 179.90),
        ("Zapatilla Skate Low Pro", "Hombre", 159.90),
        ("Chunky Sneaker Cyber", "Mujer", 189.90),
        ("Zapatilla High Top Street", "Hombre", 199.90),
        ("Slip-On Canvas Classic", "Unisex", 119.90),
        ("Sneaker Plataforma Chic", "Mujer", 175.00),
        ("Zapatilla Trail Runner", "Hombre", 209.90),
        ("Sneaker Court Vintage", "Unisex", 165.00),
        ("Zapatilla Deportiva Air", "Hombre", 219.90),
        ("Sneaker Minimalist Eco", "Mujer", 145.00),
        ("Zapatilla Basket Mid", "Hombre", 189.90),
        ("Sneaker Holográfico Glow", "Mujer", 169.00),
        ("Zapatilla Streetwear Nero", "Unisex", 159.00),
        ("Sneaker Runner Pulse", "Mujer", 179.00),
        ("Zapatilla Vulcanizada Black", "Hombre", 139.90),
        ("Sneaker Pastel Breeze", "Mujer", 155.00),
        ("Zapatilla Retro Leather", "Hombre", 185.00),
        ("Sneaker Future Tech", "Unisex", 229.90),
    ],
    2: [ # Polos (2)
        ("Polo Oversize", "Unisex", 49.90),
        ("Polo Básico Premium", "Unisex", 39.90),
        ("Polo Graphic Anime", "Unisex", 55.00),
        ("Polo Heavyweight Vintage", "Hombre", 59.90),
        ("Polo Boxy Fit Street", "Unisex", 52.00),
        ("Polo Piqué Clásico", "Hombre", 48.00),
        ("Polo Crop Top Urban", "Mujer", 42.00),
        ("Polo Tie Dye Sunset", "Unisex", 58.00),
        ("Polo Minimalist Outline", "Mujer", 45.00),
        ("Polo Graphic Cyberpunk", "Hombre", 59.00),
        ("Polo Rayas Marineras", "Unisex", 49.00),
        ("Polo Cuello Henley", "Hombre", 54.00),
        ("Polo Oversize Acid Wash", "Unisex", 62.00),
        ("Polo Baby Tee 90s", "Mujer", 39.00),
        ("Polo Tipográfico Manifesto", "Hombre", 52.00),
        ("Polo Básico Cuello V", "Mujer", 38.00),
        ("Polo Streetwear Tokyo", "Unisex", 56.00),
        ("Polo Manga Larga Waffle", "Hombre", 65.00),
        ("Polo Estampado Floral Dark", "Mujer", 49.90),
        ("Polo Skate Skull", "Hombre", 55.00),
    ],
    6: [ # Casacas & Abrigos (6)
        ("Casaca Impermeable", "Unisex", 139.90),
        ("Casaca Denim Oversize", "Unisex", 149.90),
        ("Puffer Jacket Térmica", "Unisex", 199.90),
        ("Bomber Jacket Clásica", "Hombre", 179.90),
        ("Casaca Cuero Sintético Biker", "Mujer", 189.90),
        ("Cortavientos Windbreaker", "Unisex", 129.90),
        ("Casaca Sherpa Vintage", "Hombre", 185.00),
        ("Parka Larga Polar", "Unisex", 239.90),
        ("Trench Coat Elegante", "Mujer", 219.00),
        ("Casaca Universitaria Varsity", "Unisex", 169.90),
        ("Casaca Acolchada Light", "Mujer", 159.00),
        ("Chaqueta Militar Cargo", "Hombre", 175.00),
        ("Casaca Denim Ripped", "Mujer", 149.00),
        ("Sobrecamisa Pana Gruesa", "Hombre", 139.00),
        ("Blazer Casual Oversize", "Mujer", 179.00),
        ("Casaca Deportiva Track", "Unisex", 119.90),
        ("Casaca Biker Cuero Negro", "Hombre", 209.00),
        ("Parka Impermeable Camo", "Unisex", 195.00),
        ("Casaca Teddy Fleece Soft", "Mujer", 149.90),
        ("Chaqueta Utility Multipockets", "Hombre", 189.00),
    ],
    3: [ # Pantalones & Jeans (3)
        ("Jean Recto Clásico", "Mujer", 99.90),
        ("Jean Skinny Ajustado", "Hombre", 109.90),
        ("Pantalón Cargo Black", "Hombre", 119.90),
        ("Jean Baggy Wide Leg", "Mujer", 115.00),
        ("Pantalón Jogger Urbano", "Unisex", 89.90),
        ("Pantalón Chino Slim Fit", "Hombre", 99.00),
        ("Jean Mom Fit Vintage", "Mujer", 105.00),
        ("Pantalón Parachute Y2K", "Unisex", 125.00),
        ("Jean Carpintero Drill", "Hombre", 119.00),
        ("Pantalón Flare Acampanado", "Mujer", 109.00),
        ("Pantalón Cargo Beige", "Unisex", 118.00),
        ("Jean Ripped Distressed", "Hombre", 114.90),
        ("Pantalón Palazzo Elegante", "Mujer", 98.00),
        ("Pantalón Drill Relaxed", "Hombre", 95.00),
        ("Jean Slouchy Tiro Alto", "Mujer", 108.00),
        ("Pantalón Track Pant Street", "Unisex", 89.00),
        ("Jean Recto Lavado Claro", "Hombre", 105.00),
        ("Pantalón Sastrero Casual", "Mujer", 115.00),
        ("Jean Cargo Militar", "Hombre", 129.90),
        ("Pantalón Corduroy Pana", "Unisex", 119.00),
    ],
    7: [ # Gorras y Chullos (7)
        ("Gorra Trucker LMTL", "Unisex", 39.90),
        ("Beanie Urbano Acanalado", "Unisex", 35.00),
        ("Gorra Snapback Streetwear", "Hombre", 45.00),
        ("Bucket Hat Pescador", "Unisex", 42.00),
        ("Chullo Andino Tradicional", "Unisex", 39.00),
        ("Gorra Dad Hat Clásica", "Mujer", 38.00),
        ("Gorra 5 Paneles Camper", "Unisex", 44.00),
        ("Gorra Béisbol Vintage", "Hombre", 40.00),
        ("Beanie Fisherman Corto", "Unisex", 34.00),
        ("Gorra Corduroy Retro", "Unisex", 46.00),
        ("Visera Deportiva Runner", "Mujer", 32.00),
        ("Chullo Alpaca Blend", "Unisex", 49.90),
        ("Gorra Militar Camo", "Hombre", 42.00),
        ("Bucket Hat Cuero Sintético", "Mujer", 48.00),
        ("Gorra Plana Hip Hop", "Hombre", 45.00),
        ("Beanie Pompon Invierno", "Mujer", 36.00),
        ("Gorra Denim Lavada", "Unisex", 39.90),
        ("Gorra Reflectiva Tech", "Unisex", 47.00),
        ("Gorra Bordado Minimal", "Mujer", 38.50),
        ("Chullo Polar Térmico", "Hombre", 35.00),
    ],
    1: [ # Superiores & Chompas (1)
        ("Sudadera con Capucha", "Unisex", 89.90),
        ("Camisa de lino", "Hombre", 51.50),
        ("Chompa con diseño", "Mujer", 50.00),
        ("Hoodie Oversize Acid", "Unisex", 99.90),
        ("Chompa Cuello Tortuga", "Mujer", 79.90),
        ("Camisa Oxford Cuadros", "Hombre", 69.90),
        ("Cardigan Tejido Suave", "Mujer", 85.00),
        ("Sudadera Cuello Redondo", "Unisex", 75.00),
        ("Camisa Manga Corta Resort", "Hombre", 59.00),
        ("Chompa Rayas Chunky", "Mujer", 89.00),
        ("Hoodie Zipper Frontal", "Unisex", 95.00),
        ("Camisa Vaquera Denim", "Hombre", 79.00),
        ("Chaleco Tejido Knit", "Mujer", 62.00),
        ("Chompa Cuello V Clásica", "Hombre", 72.00),
        ("Sudadera Cropped Fleece", "Mujer", 68.00),
        ("Camisa Franela Lumberjack", "Hombre", 74.00),
        ("Chompa Alpaca Suave", "Unisex", 109.90),
        ("Hoodie Boxy Gráfico", "Unisex", 92.00),
        ("Camisa Cuello Mao Lino", "Hombre", 65.00),
        ("Cardigan Largo Oversize", "Mujer", 95.00),
    ],
    5: [ # Accesorios & Mochilas (5)
        ("Mochila Minimal", "Unisex", 79.90),
        ("Cangurera Streetwear", "Unisex", 69.90),
        ("Tote Bag Lienzo Pesado", "Unisex", 39.90),
        ("Mochila Urbana Antirrobo", "Unisex", 129.90),
        ("Crossbody Bag Compacta", "Unisex", 59.90),
        ("Billetera Cuero Bifold", "Hombre", 49.90),
        ("Lentes de Sol Retro 90s", "Unisex", 45.00),
        ("Correa Táctica Militar", "Hombre", 35.00),
        ("Neceser de Viaje Tech", "Unisex", 42.00),
        ("Mochila Roll Top Impermeable", "Unisex", 139.90),
        ("Bolso Bandolera Puffer", "Mujer", 55.00),
        ("Lentes Cat Eye Modernos", "Mujer", 48.00),
        ("Correa Cuero Hebilla Plata", "Hombre", 45.00),
        ("Porta Laptop Acolchado", "Unisex", 49.00),
        ("Bolsa Gym Duffel", "Unisex", 85.00),
        ("Billetera Monedero Slim", "Mujer", 39.00),
        ("Lentes Aviador Clásicos", "Unisex", 49.90),
        ("Cangurera Reflectiva Night", "Unisex", 52.00),
        ("Mochila Escolar Estampada", "Unisex", 69.00),
        ("Gorra Porta Llavero Set", "Unisex", 34.00),
    ],
    8: [ # Medias Urbanas (8)
        ("Medias Altas Flame", "Unisex", 19.90),
        ("Pack x3 Medias Invisibles", "Unisex", 25.00),
        ("Medias Skate Retro Rayas", "Unisex", 18.00),
        ("Medias Tie Dye Pastel", "Unisex", 22.00),
        ("Medias Altas Logo Bordado", "Unisex", 20.00),
        ("Pack x3 Medias Tobilleras", "Unisex", 24.00),
        ("Medias Térmicas Lana Merino", "Unisex", 29.90),
        ("Medias Streetwear Checker", "Unisex", 19.00),
        ("Medias Deporte Compresión", "Unisex", 28.00),
        ("Medias Altas Gráficas Anime", "Unisex", 21.00),
        ("Pack x5 Medias Negras LMLL", "Unisex", 35.00),
        ("Medias Glow in the Dark", "Unisex", 24.00),
        ("Medias Algodón Orgánico", "Unisex", 22.50),
        ("Medias Skate Smiley", "Unisex", 18.50),
        ("Medias Altas Calavera", "Hombre", 19.90),
        ("Pack x3 Medias Pastel Girl", "Mujer", 26.00),
        ("Medias Running Dry-Fit", "Unisex", 25.00),
        ("Medias Clásicas Acanaladas", "Unisex", 17.00),
        ("Medias Altas Street Graffiti", "Unisex", 21.00),
        ("Pack x2 Medias Gruesas Boot", "Hombre", 29.00),
    ],
    9: [ # Joyería & Pines (9)
        ("Cadena de Acero Cubana", "Unisex", 45.00),
        ("Collar Dije Cruz Gótica", "Unisex", 39.90),
        ("Pack x4 Pines Esmaltados Anime", "Unisex", 29.90),
        ("Anillo Acero Calavera Punk", "Hombre", 35.00),
        ("Pulsera Eslabones Gruman", "Hombre", 42.00),
        ("Collar Perlas Sintéticas Y2K", "Mujer", 48.00),
        ("Pack x3 Anillos Geométricos", "Mujer", 36.00),
        ("Pin Metálico LMLL Llama", "Unisex", 15.00),
        ("Cadena Acero Fígaro 5mm", "Unisex", 49.90),
        ("Aretes Hoop Candonga Acero", "Unisex", 28.00),
        ("Pulsera Cuero Trenzado", "Hombre", 32.00),
        ("Collar Dije Candado Street", "Unisex", 38.00),
        ("Pack x5 Pines Kawaii Pop", "Mujer", 27.00),
        ("Anillo Serpiente Regulable", "Unisex", 34.00),
        ("Cadena Tennis Circonias", "Mujer", 59.90),
        ("Pin Retro Cassette 80s", "Unisex", 14.00),
        ("Aretes Ear Cuff Sin Perforar", "Mujer", 25.00),
        ("Collar Placa Militar Personalizable", "Hombre", 45.00),
        ("Pulsera Ojo Turco Protección", "Unisex", 29.00),
        ("Set Pines Streetwear Urban", "Unisex", 26.00),
    ]
}

# Real color palette
COLORS_PALETTE = ["Negro", "Blanco", "Azul Marino", "Gris Melange", "Beige Arena", "Rojo Borgoña", "Verde Oliva"]

TALLAS_POR_CAT = {
    4: ["39", "40", "41", "42", "43"],
    2: ["S", "M", "L", "XL"],
    6: ["S", "M", "L", "XL"],
    3: ["28", "30", "32", "34", "36"],
    7: ["Única", "Ajustable"],
    1: ["S", "M", "L", "XL"],
    5: ["Única"],
    8: ["35-38", "39-42", "43-45"],
    9: ["Única"]
}

sql_lines = []
sql_lines.append("-- LIMPIEZA Y REGENERACION COMPLETA DE 20 PRODUCTOS POR CADA CATEGORIA (180 EN TOTAL)")
sql_lines.append("TRUNCATE TABLE producto_detalles, productos RESTART IDENTITY CASCADE;")
sql_lines.append("")

prod_id = 1
detalle_id = 1

for cat_id in sorted(CAT_PRODUCTS.keys()):
    prods = CAT_PRODUCTS[cat_id]
    images = IMAGES[cat_id]
    tallas = TALLAS_POR_CAT[cat_id]
    
    for i, (nombre, genero, precio) in enumerate(prods):
        img = images[i % len(images)]
        sql_lines.append(f"INSERT INTO productos (id, categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES ({prod_id}, {cat_id}, '{nombre}', '{genero}', '{img}', {precio:.2f}, true);")
        
        # Generar 3 a 4 variantes con diferentes tallas y colores
        selected_colors = COLORS_PALETTE[:3] if cat_id not in [5, 9] else ["Negro", "Plateado", "Dorado"]
        
        for c_idx, color in enumerate(selected_colors):
            for t_idx, talla in enumerate(tallas[:3] if len(tallas) > 3 else tallas):
                codigo = f"PRD{prod_id:03d}-C{c_idx+1}T{t_idx+1}"
                nombre_comp = f"{nombre} {color} Talla {talla}"
                desc = f"Prenda original LMLL en color {color}. Alta resistencia y confort."
                stock = 25 + ((prod_id * 7 + c_idx * 13 + t_idx * 5) % 45)
                
                sql_lines.append(f"INSERT INTO producto_detalles (id, producto_id, nombre_completo, codigo, imagen_url, marca, talla, color, descripcion, stock, precio_base, precio_adicional) VALUES ({detalle_id}, {prod_id}, '{nombre_comp}', '{codigo}', '{img}', 'LMLL', '{talla}', '{color}', '{desc}', {stock}, {precio:.2f}, 0.00);")
                detalle_id += 1
                
        prod_id += 1

sql_lines.append("")
sql_lines.append(f"SELECT setval('productos_id_seq', {prod_id - 1});")
sql_lines.append(f"SELECT setval('producto_detalles_id_seq', {detalle_id - 1});")

with open("seed_20_por_bloque.sql", "w", encoding="utf-8") as f:
    f.write("\n".join(sql_lines))

print(f"Generado script con {prod_id - 1} productos y {detalle_id - 1} variantes.")
