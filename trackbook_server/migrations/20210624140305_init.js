
exports.up = function(knex) {
    return knex.schema.createTable("users", tableBuilder => {
        tableBuilder.increments("id").primary("USER_ID").unsigned();
        tableBuilder.string("email").notNullable().unique("USER_EMAIL");
        tableBuilder.string("password", 32).notNullable();
        tableBuilder.string("pen_name", 16).notNullable();
        tableBuilder.timestamp("registration_date").defaultTo(knex.fn.now());
    }).createTable("books", tableBuilder => {
        tableBuilder.increments("id").primary("BOOK_ID").unsigned();
        tableBuilder.string("isbn", 13).notNullable().unique("BOOK_ISBN");
        tableBuilder.string("title", 100).notNullable();
    }).createTable("readings", tableBuilder => {
        tableBuilder.increments().primary().unsigned();
        tableBuilder.string("title", 100).notNullable();
        tableBuilder.bigInteger("page_read").notNullable().defaultTo(0);
        tableBuilder.string("color").notNullable();
        tableBuilder.bigInteger("user_id").unsigned().notNullable();
        tableBuilder.bigInteger("book_id").unsigned().notNullable();
        tableBuilder.foreign("user_id")
            .references("id")
            .inTable("users")
            .onDelete("CASCADE");
        tableBuilder.foreign("book_id")
            .references("id")
            .inTable("books")
            .onDelete("CASCADE");
    });
};

exports.down = function(knex) {
    return knex.schema.dropTable("readings")
        .dropTable("books")
        .dropTable("users");
};
