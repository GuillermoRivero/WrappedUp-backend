-- Drop the existing reviews table
DROP TABLE IF EXISTS reviews;

-- Create the new reviews table with the correct schema
CREATE TABLE reviews (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    book_id BINARY(16) NOT NULL,
    text TEXT,
    rating INT NOT NULL,
    start_date DATE,
    end_date DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
); 