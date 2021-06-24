
exports.seed = function(knex) {
  // Deletes ALL existing entries
  return knex('books').del()
    .then(function () {
      // Inserts seed entries
      return knex('books').insert([
          {"isbn": "3692364658333"},
          {"isbn": "0897847079661", "title": "Carroll"},
          {"isbn": "7643259528381", "title": "White"},
          {"isbn": "7969733904599"},
          {"isbn": "7388035101710"},
          {"isbn": "1420663392379", "title": "Schmidt"},
          {"isbn": "4442212868289"},
          {"isbn": "7755139856509", "title": "Shanahan"},
          {"isbn": "5649409442069", "title": "Towne"},
          {"isbn": "5648107212116"},
          {"isbn": "4288457338890"},
          {"isbn": "2059344989437"},
          {"isbn": "4311245615999"},
          {"isbn": "4824142177765", "title": "Rowe"},
          {"isbn": "9633788332052", "title": "Lowe"},
          {"isbn": "7715210013726", "title": "Crist"},
          {"isbn": "9193912771756", "title": "Roberts"},
          {"isbn": "6335969522156"},
          {"isbn": "7274136758652", "title": "Bosco"},
          {"isbn": "8451641489214", "title": "Demore"}
      ]);
    });
};
