While: <% int i = 0; %><t:while cond="i < 3"> ${i}<% i++; %><t:if cond="i == 1"><t:continue></t:if></t:while>