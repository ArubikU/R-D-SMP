import React from 'react';

interface RoleCardProps {
    role: {
        name: string;
        pros: string;
        cons: string;
        icon: string;
    };
}

export const RoleCard: React.FC<RoleCardProps> = ({ role }) => (
    <div className="mc-card bg-zinc-900 p-4 relative overflow-hidden border border-gray-700">
        <div className="flex justify-between items-start mb-2 relative z-10">
            <span className="text-3xl">{role.icon}</span>
            <span className="text-xs uppercase bg-gray-800 px-2 py-1 rounded text-gray-400">Personal</span>
        </div>
        <h3 className="text-xl text-white mb-2 relative z-10 font-bold">{role.name}</h3>
        <div className="space-y-2 relative z-10 text-sm">
            <div className="flex items-start gap-2">
                <span className="text-green-500 font-bold">+</span>
                <p className="text-gray-400 leading-tight">{role.pros}</p>
            </div>
            <div className="flex items-start gap-2">
                <span className="text-red-500 font-bold">-</span>
                <p className="text-gray-400 leading-tight">{role.cons}</p>
            </div>
        </div>
    </div>
);
