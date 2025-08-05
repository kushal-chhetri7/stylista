package com.kushal.stylista.utils;

import com.kushal.stylista.model.ClothModel;
import com.kushal.stylista.model.FavoriteModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyData {
    private static final String[] images = {"https://img.freepik.com/free-photo/interior-kids-room-decoration-with-clothes_23-2149096034.jpg?semt=ais_hybrid", "https://img.freepik.com/free-photo/still-life-with-classic-shirts-hanger_23-2150828629.jpg?semt=ais_hybrid","https://img.freepik.com/free-photo/woman-showing-clothes-customer_23-2148929526.jpg?semt=ais_hybrid","https://img.freepik.com/free-photo/still-life-with-classic-shirts-hanger_23-2150828620.jpg?t=st=1743093561~exp=1743097161~hmac=dd8220d85798c534f9afbbe2ba95c63de18d761f90beee8a11a582df08a85ecb&w=740","https://img.freepik.com/free-photo/still-life-say-no-fast-fashion_23-2149669576.jpg?t=st=1743093565~exp=1743097165~hmac=fb9a4253ade77248676674ec9cd25100d4aade5a8cb821274175422d3a23463b&w=740"};


    public static List<FavoriteModel> getFavoriteList(){

        List<FavoriteModel> dummyList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            dummyList.add(new FavoriteModel(
                    String.valueOf(i),
                    "Product " + i,
                    (double) (Math.round(Math.random() * 10000.0) / 100.0), // Random price between 0.00 and 100.00
                    images[(int) (Math.random() * images.length)]
            ));
        }
        return dummyList;
    }

    public static String getDummyImage(){
        return images[(int) (Math.random() * images.length)];
    }

    public static List<ClothModel> getDummyClothes() {
        List<ClothModel> clothes = new ArrayList<>();

        String[] names = {"Denim Jacket", "Cotton T-Shirt", "Slim Fit Chinos", "Summer Dress", "Ankle Boots"};
        String[] categories = {"Jackets", "T-Shirts", "Pants", "Dresses", "Footwear"};
        String[] subcategories = {"Men", "Unisex", "Men", "Women", "Unisex"};
        String[] brands = {"UrbanStyle", "CottonKing", "FitWear", "FloralGrace", "BootMasters"};
        double[] prices = {59.99, 19.99, 39.99, 49.99, 89.99};
        String[] descriptions = {
                "Stylish denim jacket, great for all seasons.",
                "Soft cotton T-shirt, available in various colors.",
                "Comfortable slim-fit chinos made from premium fabric.",
                "Lightweight floral dress for summer outings.",
                "Leather ankle boots with durable soles."
        };

        List<List<String>> colorsList = Arrays.asList(
                Arrays.asList("Blue", "Black", "Grey"),
                Arrays.asList("White", "Black", "Red", "Green"),
                Arrays.asList("Beige", "Navy", "Olive"),
                Arrays.asList("Yellow", "Pink", "Blue"),
                Arrays.asList("Black", "Brown")
        );
        List<List<String>> sizesList = Arrays.asList(
                Arrays.asList("S", "M", "L", "XL"),
                Arrays.asList("XS", "S", "M", "L", "XL", "XXL"),
                Arrays.asList("30", "32", "34", "36", "38"),
                Arrays.asList("S", "M", "L"),
                Arrays.asList("7", "8", "9", "10", "11")
        );

        List<List<String>> materialsList = Arrays.asList(
                Arrays.asList("Denim", "Cotton"),
                Arrays.asList("Cotton", "Polyester"),
                Arrays.asList("Cotton", "Spandex"),
                Arrays.asList("Polyester", "Floral Print"),
                Arrays.asList("Leather", "Rubber Sole")
        );

        List<List<String>> tagsList = Arrays.asList(
                Arrays.asList("casual", "denim", "winter"),
                Arrays.asList("soft", "comfortable", "casual"),
                Arrays.asList("slim-fit", "chinos", "comfortable"),
                Arrays.asList("summer", "floral", "dress"),
                Arrays.asList("footwear", "leather", "boots")
        );

        for (int i = 0; i < names.length; i++) {
            ClothModel cloth = new ClothModel();

            cloth.setId(String.valueOf(i + 1));  // ID as a string
            cloth.setName(names[i]);
            cloth.setCategory(categories[i]);
            cloth.setSubcategory(subcategories[i]);
            cloth.setBrand(brands[i]);
            cloth.setPrice(prices[i]);
            cloth.setDescription(descriptions[i]);
            cloth.setMaterials(new ArrayList<>(materialsList.get(i)));
            cloth.setTags(new ArrayList<>(tagsList.get(i)));

            List<ClothModel.Color> colors = new ArrayList<>();
            List<String> colorNames = colorsList.get(i);
            for (String colorName : colorNames) {
                ClothModel.Color color = new ClothModel.Color();
                color.setName(colorName);
                color.setHex("#000000"); // Default color for now
                color.setImages(Arrays.asList("https://example.com/images/" + colorName.toLowerCase() + "-front.jpg"));

                // Set sizes (Example: stock is randomly set for now)
                List<ClothModel.Size> sizes = new ArrayList<>();
                for (String size : sizesList.get(i)) {
                    ClothModel.Size sizeObj = new ClothModel.Size();
                    sizeObj.setSize(size);
                    sizeObj.setStock((int) (Math.random() * 20) + 1); // Random stock between 1 and 20
                    sizes.add(sizeObj);
                }
                color.setSizes(sizes);

                colors.add(color);
            }

            cloth.setColors(colors);

            clothes.add(cloth);
        }

        return clothes;
    }

}
