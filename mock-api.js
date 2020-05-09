const express = require('express');
const mockApi = express();
mockApi.get('/v2/pdp/tcin/:tcin', (req, res) => {
  res.send({
    id: req.params.tcin,
    name: 'Big Lebowski (Blu-ray) (Widescreen)'
  })
});
mockApi.listen(8081, () => console.log('Mock RedSky App Running on port 8081'));
