package cz.knihaplnaaktivit.kpa_mobile.repository;

import java.util.ArrayList;
import java.util.List;

import cz.knihaplnaaktivit.kpa_mobile.model.Product;

public class ProductRepository {
    public static List<Product> getProducts() {
        List<Product> res = new ArrayList<>();

        res.add(new Product(1, "Kniha plná aktivit", "Nabízíme vám ručně vyráběnou Knihu plnou aktivit, která je určena dětem od 3 let, avšak některé úkoly zvládají i mladší děti.\n" +
                "\n" +
                "Tato didaktická pomůcka vychází z principů strukturovaného učení a nese prvky používané v rámci Montessori pedagogiky. Je vhodná i pro děti se specifickými vzdělávacími potřebami.\n" +
                "\n" +
                "Kniha je tvořena 12 jednotlivými laminovanými pracovními listy, které obsahují úkoly zaměřené např. na rozvíjení jemné motoriky, poznávání a třídění obrázků (podle barev, tvarů apod.), tvorbu řad či rozřazení do skupin podle logických souvislostí.\n" +
                "Listy jsou doplněny různými materiály pro zvýšení atraktivity pro děti, jakými jsou např. barevné kolíčky, pompony, stuhy. Řada z nich je opatřena obrázky se suchým zipem, který dětem přináší další možnosti práce s nimi. Jednotlivé stránky jsou řazeny v kroužkovém pořadači, což umožňuje snadnější manipulaci s nimi.\n" +
                "\n" +
                "Tuto Knihu plnou aktivit lze doplnit našimi dalšími pomůckami.\n" +
                "\n" +
                "Při objednání více kusů či různých druhů didaktických pomůcek se poštovné a balné odvíjí od tarifů České pošty.\n" +
                "Použité materiály se mohou lišit od vyobrazených.", 440));
        res.add(new Product(2, "Abeceda", "Abeceda je určena dětem od 3 let, tvoří ji 6 pracovních zalaminovaných listů velikosti A4 a 54 malých obrázkových kartiček. Děti rozřazují obrázkové kartičky podle počátečního písmena ke stejnému písmenu na pracovním listě pomocí suchého zipu.\n" +
                "\n" +
                " \n" +
                "\n" +
                "Při objednání více kusů či různých druhů didaktické pomůcek se poštovné a balné odvíjí od tarifů České pošty.", 200));
        res.add(new Product(3, "Velikonoce", "Řada pracovních listů zaměřených na období jara, kterými můžete doplnit svou Knihu plnou aktivit, či s nimi pracovat samostatně.\n" +
                "\n" +
                "Pomůcka je určena dětem od 3 let. Ručně vyráběné listy jsou zalaminovány a úkoly se plné zejména přiřazováním správných řešení pomocí suchých zipů. Zaměření této didaktické pomůcky je na rozvoj motoriky a hledání logických posloupností.\n" +
                "\n" +
                "Použité materiály se mohou lišit od vyobrazených.\n" +
                "\n" +
                "Při objednání více kusů či různých didaktických pomůcek se poštovné a balné odvíjí od tarifů České pošty.", 350));
        res.add(new Product(4, "Pomucka 1", "Popis", 100));
        res.add(new Product(5, "Pomucka 2", "Popis", 100));
        res.add(new Product(6, "Pomucka 3", "Popis", 100));
        res.add(new Product(7, "Pomucka 4", "Popis", 100));
        res.add(new Product(8, "Pomucka 5", "Popis", 100));

        return res;
        //return ApiConnector.get(ApiConnector.Type.PRODUCTS);
    }
}
