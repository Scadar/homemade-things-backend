package ru.homemadethings.homemadethings.auth.model.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateLastNameRequest {
    private String lastName;
}
