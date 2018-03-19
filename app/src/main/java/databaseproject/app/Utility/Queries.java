package databaseproject.app.Utility;

public enum Queries {

    GETALLPRODUCTS("SELECT * FROM PRODUCT;"),
    GETCARTOFUSER("SELECT " +
            "CART.C_ID, CART.P_ID, CART.U_ID, CART.QUANTITY, CART.P_ID, " +
            "PRODUCT.PRODUCT_NAME, PRODUCT.CATEGORY, PRODUCT.IMAGE " +
            "FROM CART " +
            "JOIN PRODUCT " +
            "ON " +
            "CART.P_ID = PRODUCT.P_ID " +
            "WHERE CART.U_ID = ");

    public String e;

    Queries(String entity)    {
        this.e = entity;
    }
}
