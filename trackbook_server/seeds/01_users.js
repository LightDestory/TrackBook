
exports.seed = function(knex) {
  // Deletes ALL existing entries
  return knex('users').del()
    .then(function () {
      // Inserts seed entries
      return knex('users').insert([
          { "email": "dewitt.pouros@example.com", "pen_name": "Brielle", "password": "6e6bc4e49dd477ebc98ef4046c067b5f" },
          { "email": "goldner.ahmed@example.org", "pen_name": "Jacey", "password": "6e6bc4e49dd477ebc98ef4046c067b5f" },
          { "email": "sarai65@example.com", "pen_name": "Ollie", "password": "6e6bc4e49dd477ebc98ef4046c067b5f" },
          { "email": "williamson.tyrell@example.net", "pen_name": "Brenda", "password": "6e6bc4e49dd477ebc98ef4046c067b5f" },
          { "email": "marvin.orion@example.com", "pen_name": "Adriel", "password": "6e6bc4e49dd477ebc98ef4046c067b5f" }
      ]);
    });
};
