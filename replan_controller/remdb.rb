

#endpoint = "http://replan-api.herokuapp.com/replan"
#endpoint = "http://platform.supersede.eu:8280/replan"
endpoint = "http://supersede.es.atos.net:8280/replan"

skids = Hash.new
rsids = Hash.new
ftids = Hash.new
rlids = Hash.new

#[35].each do |i|   
[1,2,3].each do |i|

   #endpoint = "http://62.14.219.13:8280/replan" if i == 3
   
   ## Project
   pjson= JSON.parse(RestClient.get("#{endpoint}/projects/#{i}").body)
   
   p = Project.create(name: pjson["name"], description: pjson["description"], \
                     effort_unit: pjson["effort_unit"], hours_per_effort_unit: pjson["hours_per_effort_unit"], \
                     hours_per_week_and_full_time_resource: pjson["hours_per_week_and_full_time_resource"])
   
   ## Skills
   
   skills = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/skills").body)
   skills.each do |s|
      ns = p.skills.create(name: s["name"], description: s["description"])
      skids[s["id"]] = ns.id
   end
   
   ## Resources
   resources = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/resources").body)
   resources.each do |r|
      nr = p.resources.create(name: r["name"], description: r["description"], availability: r["availability"].to_f)
      rsids[r["id"]] = nr.id
      r["skills"].each do |rs|
        nr.skills << Skill.find(skids[rs["id"]])
      end
   end
   
   ## Features
   dependencies = Hash.new
   features = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/features").body)
   features.each do |f|
      nf = p.features.create(code: f["code"], name: f["name"], description: f["description"], \
                          deadline: f["deadline"], priority: f["priority"], \
                          effort: f["effort"].to_f, jira_url: f["jira_url"])
      ftids[f["id"]] = nf.id
      f["required_skills"].each do |rs|
        nf.required_skills << Skill.find(skids[rs["id"]])
      end
      depi = Array.new
      f["depends_on"].each do |d|
        depi << d["id"]
      end
      dependencies[nf.id] = depi unless depi.empty? 
   end
   
   dependencies.each do |key, value|
     fi = Feature.find(key)
     value.each do |id|
        fi.depends_on << Feature.find(ftids[id])
     end
   end
   
   ## Releases 
   releases = JSON.parse(RestClient.get("#{endpoint}/projects/#{i}/releases").body)
   releases.each do |l|
      nl = p.releases.create(name: l["name"], description: l["description"], \
                          deadline: l["deadline"], starts_at: l["starts_at"])
      rlids[l["id"]] = nl.id
      l["resources"].each do |r|
        nl.resources << Resource.find(rsids[r["id"]])
      end
      uri = "#{endpoint}/projects/#{i}/releases/"+ l["id"].to_s + "/features"
      JSON.parse(RestClient.get(uri).body).each do |f|
        nl.features << Feature.find(ftids[f["id"]])
      end
      nl.plan.destroy unless nl.plan.nil?
      
   end
   puts p.name
end
