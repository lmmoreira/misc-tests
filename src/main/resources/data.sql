INSERT INTO "marketplace" ("name") VALUES
                                   ('Amazon'),
                                   ('eBay'),
                                   ('Alibaba');

INSERT INTO "seller" ("name") VALUES
                                  ('Seller 1'),
                                  ('Seller 2'),
                                  ('Seller 3');

INSERT INTO "client" ("name") VALUES
                              ('Client 1'),
                              ('Client 2'),
                              ('Client 3');

INSERT INTO "order" ("observation", "seller", "client", "marketplace", "status") VALUES
                                  ('Observation 1', 1, 1, 1, 'NEW'),
                                  ('Observation 2', 2, 2, 2, 'NEW');
