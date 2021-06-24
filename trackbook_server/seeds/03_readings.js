
exports.seed = function(knex) {
  // Deletes ALL existing entries
  return knex('readings').del()
    .then(function () {
      // Inserts seed entries
      return knex('readings').insert([
          {"user_id": 1, "book_id": 1, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 2, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 3, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 4, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 5, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 6, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 7, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 8, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 9, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 10, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 11, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 12, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 13, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 14, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 15, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 16, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 17, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 18, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 19, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 20, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 1, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 2, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 3, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 4, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 5, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 6, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 7, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 8, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 9, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 10, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 11, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 12, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 13, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 14, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 15, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 16, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 17, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 18, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 19, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 20, "title": "CustomTitle" },
          {"user_id": 1, "book_id": 1, "title": "CustomTitle" },
          {"user_id": 2, "book_id": 2, "title": "CustomTitle" },
          {"user_id": 3, "book_id": 3, "title": "CustomTitle" },
          {"user_id": 4, "book_id": 4, "title": "CustomTitle" },
          {"user_id": 5, "book_id": 5, "title": "CustomTitle" }
      ]);
    });
};
