
exports.seed = function(knex) {
  // Deletes ALL existing entries
  return knex('books').del()
    .then(function () {
      // Inserts seed entries
      return knex('books').insert([
          {"isbn": "3692364658333", "title": "Carroll"},
          {"isbn": "0897847079661", "title": "Pub"},
          {"isbn": "7643259528381", "title": "White"},
          {"isbn": "7969733904599", "title": "Merrys"},
          {"isbn": "7388035101710", "title": "Qwerty"},
          {"isbn": "1420663392379", "title": "Schmidt"},
          {"isbn": "4442212868289", "title": "Pola"},
          {"isbn": "7755139856509", "title": "Shanahan"},
          {"isbn": "5649409442069", "title": "Towne"},
          {"isbn": "5648107212116", "title": "Fig"},
          {"isbn": "4288457338890", "title": "Kail"},
          {"isbn": "2059344989437", "title": "Pyra"},
          {"isbn": "4311245615999", "title": "Saint"},
          {"isbn": "4824142177765", "title": "Rowe"},
          {"isbn": "9633788332052", "title": "Lowe"},
          {"isbn": "7715210013726", "title": "Crist"},
          {"isbn": "9193912771756", "title": "Roberts"},
          {"isbn": "6335969522156", "title": "Nersa"},
          {"isbn": "7274136758652", "title": "Bosco"},
          {"isbn": "8451641489214", "title": "Demore"}
      ]);
    });
};
