package com.trung.springredisapp.controller;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UsersRepository usersRepository;

    @RequestMapping
    public ResponseEntity<Map<String, User>> get(@RequestParam(value = "ids") String idsString) {
        Set<Integer> ids = parseIds(idsString);

        Map<String, User> usersMap = new HashMap<>();

        for (Integer id : ids) {
            User user = usersRepository.getUserById(id);
            if (user == null){
                LOGGER.debug("User not found by id: "+id);
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.BAD_REQUEST);
            }
            usersMap.put(String.valueOf(user.getId()), user);
        }

        return new ResponseEntity<>(usersMap, HttpStatus.OK);
    }
    private Set<Integer> parseIds(String idsString){
        return Arrays.stream(idsString.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }


    @RequestMapping(value = "/me")
    public ResponseEntity<User> getMe(Model model, HttpSession session) {
        String user = (String) session.getAttribute(SessionAttrs.CHAT_USER_NAME);
        if (user == null){
            LOGGER.debug("User not found in session by attribute: "+SessionAttrs.CHAT_USER_NAME);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        Gson gson = new Gson();
        return new ResponseEntity<>(gson.fromJson(user, User.class), HttpStatus.OK);
    }


    @RequestMapping(value = "/online")
    public ResponseEntity<Map<String, User>> getOnlineUsers() {
        Map<String, User> usersMap = new HashMap<>();

        Set<Integer> onlineIds = usersRepository.getOnlineUsersIds();
        if( Objects.isNull(onlineIds) ) {
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        }

        for(var onlineId : onlineIds) {
            User user = usersRepository.getUserById(onlineId);
            if (user == null){
                LOGGER.debug("User not found by id: "+onlineId);
                return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            usersMap.put(String.valueOf(user.getId()), user);
        }

        return new ResponseEntity<>(usersMap, HttpStatus.OK);
    }
}