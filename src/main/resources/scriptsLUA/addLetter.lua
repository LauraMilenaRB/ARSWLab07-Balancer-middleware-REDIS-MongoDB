-- addLetter.lua
local wordc= redis.call('HGET', KEYS[1],"word")
local guessedWord = redis.call('HGET', KEYS[1] ,"guessedword")
local newGuessed=""

for i=1, string.len(wordc) do
	if string.sub(wordc,i,i) == ARGV[1] 
	then 
		newGuessed=newGuessed..ARGV[1]
	else
		newGuessed=newGuessed..string.sub(guessedWord,i,i)
	end
end

redis.call('HSET', KEYS[1], "guessedword" , newGuessed)

return newGuessed